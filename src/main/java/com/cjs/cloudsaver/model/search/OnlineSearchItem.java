package com.cjs.cloudsaver.model.search;

import lombok.Data;

@Data
public class OnlineSearchItem {
    private String mediaType;
    private int tmdbid;
    private String poster;
    private String title;
    private String overview;
    private int flg115;
    private double voteAverage;
    private String releaseDate;

    public OnlineSearchItem(String mediaType, int tmdbid, String poster, String title, String overview,
                            int flg115, double voteAverage, String releaseDate) {
        this.mediaType = mediaType;
        this.tmdbid = tmdbid;
        this.poster = poster;
        this.title = title;
        this.overview = overview;
        this.flg115 = flg115;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }
}
