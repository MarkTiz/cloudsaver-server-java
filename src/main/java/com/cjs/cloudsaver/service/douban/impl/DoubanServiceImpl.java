package com.cjs.cloudsaver.service.douban.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cjs.cloudsaver.model.douban.DoubanSubject;
import com.cjs.cloudsaver.service.douban.DoubanService;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class DoubanServiceImpl implements DoubanService {


    private final OkHttpClient client = new OkHttpClient();

    private static final String BASE_URL = "https://movie.douban.com/j/search_subjects";

    // 获取豆瓣热门列表
    public List<DoubanSubject> getHotList(String type, String tag, int pageLimit, int pageStart) {
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(BASE_URL)).newBuilder()
                .addQueryParameter("type", type)
                .addQueryParameter("tag", tag)
                .addQueryParameter("page_limit", String.valueOf(pageLimit))
                .addQueryParameter("page_start", String.valueOf(pageStart))
                .build();

        // 请求
        Request request = new Request.Builder()
                .url(url)
                .addHeader("accept", "*/*")
                .addHeader("accept-language", "zh-CN,zh;q=0.9,en;q=0.8")
                .addHeader("x-requested-with", "XMLHttpRequest")
                .addHeader("Referer", "https://movie.douban.com/")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("请求失败，状态码: " + response.code());
            }
            String responseBody = response.body().string();
            JSONObject responseJson = JSON.parseObject(responseBody);

            List<DoubanSubject> subjects = new ArrayList<>();
            if (responseJson != null && responseJson.containsKey("subjects")) {
                subjects = JSON.parseArray(responseJson.getString("subjects"), DoubanSubject.class);
            }
            return subjects;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
