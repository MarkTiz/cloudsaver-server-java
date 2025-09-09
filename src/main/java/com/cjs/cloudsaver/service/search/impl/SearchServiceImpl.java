package com.cjs.cloudsaver.service.search.impl;

import com.alibaba.fastjson2.JSON;

import com.cjs.cloudsaver.common.exception.BizException;
import com.cjs.cloudsaver.model.common.TelegramConfig;
import com.cjs.cloudsaver.model.search.*;
import com.cjs.cloudsaver.service.search.SearchService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {


    private final RestTemplate restTemplate;

    @Resource
    private final TelegramConfig telegramConfig;

    @Value("${nullbrapi.X-APP-ID}")
    private String x_app_id;

    @Value("${nullbrapi.X-API-KEY}")
    private String x_api_key;

    @Override
    public List<ChannelResult> searchAll(String keyword, String channelId, String lastMessageId) throws BizException {
        List<ChannelResult> allResults = new ArrayList<>();

        // 在线搜索
        try {
            OnlineSearchResponse onlineResults = null;
            if (!keyword.isEmpty()) {
                onlineResults = searchInOnline(keyword);
            }
            if (onlineResults != null && !onlineResults.getItems().isEmpty()) {
                ChannelInfo apiChannel = new ChannelInfo("search", "增强搜索",
                        "https://upload.wikimedia.org/wikipedia/commons/0/08/Netflix_2015_logo.svg");

                List<SourceItem> channelResults = onlineResults.getItems().stream()
                        .map(item -> new SourceItem(
                                item.getTitle(),
                                item.getOverview(),
                                "https://image.tmdb.org/t/p/w500" + item.getPoster(),
                                "pan115",
                                new ArrayList<>(),
                                apiChannel.getName(),
                                apiChannel.getId(),
                                item.getTmdbid(),
                                item.getReleaseDate(),
                                item.getVoteAverage(),
                                item.getReleaseDate(),
                                item.getMediaType()
                        ))
                        .collect(Collectors.toList());

                if (!channelResults.isEmpty()) {
                    allResults.add(new ChannelResult(channelResults, apiChannel, apiChannel.getId()));
                }
            }
        } catch (Exception e) {
            throw new BizException("api_not_complete", e.getMessage());
        }

        // 频道搜索
        List<Map<String, String>> channels = channelId.isEmpty()
                ? telegramConfig.getChannels().entrySet().stream()
                .map(e -> Map.of(e.getKey(), e.getValue()))
                .toList()
                : List.of(Map.of(channelId, telegramConfig.getChannels().get(channelId)));

        List<CompletableFuture<Void>> searchFutures = channels.stream()
                .map(channel -> CompletableFuture.runAsync(() -> {
                    try {
                        Map.Entry<String, String> entry = channel.entrySet().iterator().next();
                        String id = entry.getKey();
                        String name = entry.getValue();
                        String messageIdParams = lastMessageId.isEmpty() ? "" : "before=" + lastMessageId;
                        String url = "/" + id +
                                (keyword.isEmpty() ? "?" + messageIdParams : "?q=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8) + "&" + messageIdParams);

                        SearchWebResult results = searchInWeb(url);
                        results.getItems().sort((a, b) -> a.getCloudType().equals("pan115") ? -1 : 1);

                        if (!results.getItems().isEmpty()) {
                            List<SourceItem> channelResults = results.getItems().stream()
                                    .filter(item -> !item.getCloudLinks().isEmpty())
                                    .map(item -> new SourceItem(
                                            item.getMessageId(), item.getTitle(), item.getPubDate(),
                                            item.getContent(), item.getImage(), item.getCloudLinks(),
                                            item.getCloudType(), item.getTags(), item.getTmdbId(),
                                            item.getVote(), name, id
                                    ))
                                    .collect(Collectors.toList());

                            allResults.add(new ChannelResult(
                                    channelResults,
                                    new ChannelInfo(id, name, results.getChannelLogo()),
                                    id
                            ));
                        }
                    } catch (Exception e) {
                        System.out.println("搜索频道失败:"+e.getMessage());
                    }
                }))
                .toList();

        CompletableFuture.allOf(searchFutures.toArray(new CompletableFuture[0])).join();

        return allResults;
    }

    /**
     * CoolOnlineEmby 网页内容搜索
     */
    private OnlineSearchResponse searchInOnline(String query) throws BizException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-APP-ID", x_app_id);

            String firstPageUrl = "https://api.nullbr.eu.org/search?query=" + URLEncoder.encode(query, StandardCharsets.UTF_8) + "&page=1";
            HttpEntity<?> entity = new HttpEntity<>(headers);
            String responseBody = restTemplate.exchange(firstPageUrl, HttpMethod.GET, entity, String.class).getBody();
            OnlineSearchResponse response = JSON.parseObject(responseBody, OnlineSearchResponse.class);

            if (response == null || response.getTotalPages() == 1) {
                return response;
            }

            List<CompletableFuture<OnlineSearchResponse>> pageFutures = new ArrayList<>();
            for (int page = 2; page <= response.getTotalPages(); page++) {
                String pageUrl = "https://api.nullbr.eu.org/search?query=" + URLEncoder.encode(query, StandardCharsets.UTF_8) + "&page=" + page;
                pageFutures.add(CompletableFuture.supplyAsync(() ->
                        JSON.parseObject(restTemplate.exchange(pageUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody(),
                                OnlineSearchResponse.class)));
            }

            List<OnlineSearchItem> allItems = new ArrayList<>(response.getItems());
            CompletableFuture.allOf(pageFutures.toArray(new CompletableFuture[0])).join();
            pageFutures.forEach(future -> {
                OnlineSearchResponse pageResponse = future.join();
                if (pageResponse != null && pageResponse.getItems() != null) {
                    allItems.addAll(pageResponse.getItems());
                }
            });

            return new OnlineSearchResponse(1, response.getTotalPages(), allItems.size(), allItems);
        } catch (Exception e) {
            throw new BizException("api_not_complete", e.getMessage());
        }
    }


    private SearchWebResult searchInWeb(String url) throws BizException {
        try {
            String response = restTemplate.getForObject(telegramConfig.getBaseUrl() + url, String.class);
            if (response.isEmpty()) {
                return new SearchWebResult(new ArrayList<>(), "");
            }

            Document doc = Jsoup.parse(response);
            List<SourceItem> items = new ArrayList<>();
            String channelLogo = "";

            // 获取频道 logo
            Element logoEl = doc.selectFirst(".tgme_header_link img");
            if (logoEl != null) {
                channelLogo = logoEl.attr("src");
            }

            // 遍历每条消息
            Elements messageElements = doc.select(".tgme_widget_message_wrap");
            for (Element messageEl : messageElements) {
                // 获取 messageId
                String messageId = "";
                Element msgEl = messageEl.selectFirst(".tgme_widget_message");
                if (msgEl != null) {
                    String dataPost = msgEl.attr("data-post");
                    if (dataPost != null && dataPost.contains("/")) {
                        String[] parts = dataPost.split("/", 2);
                        messageId = parts[1];
                    } else {
                        System.out.println("data-post 格式异常:"+ dataPost);
                        continue; // 跳过这条消息
                    }
                } else {
                    System.out.println("未找到 .tgme_widget_message 元素");
                    continue;
                }

                // 提取标题和内容
                Element textEl = messageEl.selectFirst(".js-message_text");
                String html = textEl != null ? textEl.html() : "";
                String title = "";
                String content = "";
                if (!html.isEmpty()) {
                    title = html.split("<br>")[0].replaceAll("<[^>]+>", "").replaceAll("\n", "");
                    content = html.replace(title, "").split("<a")[0].replaceAll("<br>", "").trim();
                }

                // 发布时间
                String pubDate = messageEl.select("time").attr("datetime");

                // 图片
                String image = "";
                Element imgWrap = messageEl.selectFirst(".tgme_widget_message_photo_wrap");
                if (imgWrap != null) {
                    String style = imgWrap.attr("style");
                    Matcher matcher = Pattern.compile("url\\('(.+?)'\\)").matcher(style);
                    if (matcher.find()) {
                        image = matcher.group(1);
                    }
                }

                // 云盘链接和标签
                List<String> links = messageEl.select(".tgme_widget_message_text a").eachAttr("href");
                List<String> tags = messageEl.select(".tgme_widget_message_text a").stream()
                        .map(Element::text)
                        .filter(text -> text.startsWith("#"))
                        .collect(Collectors.toList());

                // 提取云盘信息
                CloudInfo cloudInfo = extractCloudLinks(String.join(" ", links));
                if ("pan115".equals(cloudInfo.getCloudType()) || "quark".equals(cloudInfo.getCloudType())) {
                    items.add(0, new SourceItem(
                            messageId,
                            title,
                            pubDate,
                            content,
                            image,
                            cloudInfo.getLinks(),
                            cloudInfo.getCloudType(),
                            tags,
                            0,
                            0
                    ));
                }
            }

            return new SearchWebResult(items, channelLogo);

        } catch (Exception e) {
            throw new BizException("api_not_complete", e.getMessage());
        }
    }

    private CloudInfo extractCloudLinks(String text) {
        List<String> links = new ArrayList<>();
        String cloudType = "";
        for (Map.Entry<String, String> entry : telegramConfig.getCloudPatterns().entrySet()) {
            Pattern pattern = Pattern.compile(entry.getValue());
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                links.add(matcher.group());
            }
            if (!links.isEmpty()) {
                cloudType = entry.getKey();
                break;
            }
        }
        return new CloudInfo(new ArrayList<>(new HashSet<>(links)), cloudType);
    }


    @Override
    public Object get115LinkByTmdbId(String tmdbId, String type) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-APP-ID", x_app_id);
        headers.set("X-API-KEY", x_api_key);

        String url = "https://api.nullbr.eu.org/" + type + "/" + tmdbId + "/115";
        HttpEntity<?> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, Object.class).getBody();
    }
}
