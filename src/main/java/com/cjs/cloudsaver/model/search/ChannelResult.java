package com.cjs.cloudsaver.model.search;

import lombok.Data;

import java.util.List;

@Data
public class ChannelResult {
    private List<SourceItem> list;
    private ChannelInfo channelInfo;
    private String id;

    public ChannelResult(List<SourceItem> list, ChannelInfo channelInfo, String id) {
        this.list = list;
        this.channelInfo = channelInfo;
        this.id = id;
    }
}
