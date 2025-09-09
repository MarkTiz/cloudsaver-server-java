package com.cjs.cloudsaver.model.search;

import lombok.Data;

import java.util.List;

@Data
public class SearchWebResult {
    private List<SourceItem> items;
    private String channelLogo;

    public SearchWebResult(List<SourceItem> items, String channelLogo) {
        this.items = items;
        this.channelLogo = channelLogo;
    }
}
