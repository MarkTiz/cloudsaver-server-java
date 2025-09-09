package com.cjs.cloudsaver.model.search;

import lombok.Data;

import java.util.List;

@Data
public class SearchResult {
    private List<ChannelResult> data;

    public SearchResult(List<ChannelResult> data) {
        this.data = data;
    }
}
