package com.cjs.cloudsaver.model.search;

import lombok.Data;

import java.util.List;

@Data
public  class OnlineSearchResponse {
    private int page;
    private int totalPages;
    private int totalResults;
    private List<OnlineSearchItem> items;

    public OnlineSearchResponse(int page, int totalPages, int totalResults, List<OnlineSearchItem> items) {
        this.page = page;
        this.totalPages = totalPages;
        this.totalResults = totalResults;
        this.items = items;
    }
}
