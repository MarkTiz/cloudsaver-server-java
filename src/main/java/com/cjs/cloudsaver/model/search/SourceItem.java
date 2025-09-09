package com.cjs.cloudsaver.model.search;

import lombok.Data;

import java.util.List;

@Data
public class SourceItem {
    private String messageId;
    private String title;
    private String pubDate;
    private String content;
    private String image;
    private List<String> cloudLinks;
    private String cloudType;
    private List<String> tags;
    private int tmdbId;
    private double vote;
    private String channel;
    private String channelId;
    private String date;
    private String type;

    public SourceItem(String messageId, String title, String pubDate, String content, String image,
                      List<String> cloudLinks, String cloudType, List<String> tags, int tmdbId, double vote) {
        this.messageId = messageId;
        this.title = title;
        this.pubDate = pubDate;
        this.content = content;
        this.image = image;
        this.cloudLinks = cloudLinks;
        this.cloudType = cloudType;
        this.tags = tags;
        this.tmdbId = tmdbId;
        this.vote = vote;
    }

    public SourceItem(String messageId, String title, String pubDate, String content, String image,
                      List<String> cloudLinks, String cloudType, List<String> tags, int tmdbId, double vote,
                      String channel, String channelId) {
        this.messageId = messageId;
        this.title = title;
        this.pubDate = pubDate;
        this.content = content;
        this.image = image;
        this.cloudLinks = cloudLinks;
        this.cloudType = cloudType;
        this.tags = tags;
        this.tmdbId = tmdbId;
        this.vote = vote;
        this.channel = channel;
        this.channelId = channelId;
    }

    public SourceItem(String title, String content, String image, String cloudType,
                      List<String> cloudLinks, String channel, String channelId,
                      int tmdbId, String date, double vote, String releaseDate, String mediaType) {
        this.title = title;
        this.content = content;
        this.image = image;
        this.cloudType = cloudType;
        this.cloudLinks = cloudLinks;
        this.channel = channel;
        this.channelId = channelId;
        this.tmdbId = tmdbId;
        this.date = date;
        this.vote = vote;
        this.type = mediaType;
    }
}
