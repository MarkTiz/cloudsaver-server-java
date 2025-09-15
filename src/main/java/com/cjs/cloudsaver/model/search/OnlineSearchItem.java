package com.cjs.cloudsaver.model.search;

import lombok.Data;

@Data
public class OnlineSearchItem {
    private String media_type;
    private int tmdbid;
    private String poster;
    private String title;
    private String overview;
    private int flg115;
    private double vote_average;
    private String release_date;

    public OnlineSearchItem(String media_type, int tmdbid, String poster, String title, String overview,
                            int flg115, double vote_average, String release_date) {
        this.media_type = media_type;
        this.tmdbid = tmdbid;
        this.poster = poster;
        this.title = title;
        this.overview = overview;
        this.flg115 = flg115;
        this.vote_average = vote_average;
        this.release_date = release_date;
    }
}
