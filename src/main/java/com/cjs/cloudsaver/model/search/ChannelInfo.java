package com.cjs.cloudsaver.model.search;

import lombok.Data;

@Data
public class ChannelInfo {
    private String id;
    private String name;
    private String channelLogo;

    public ChannelInfo(String id, String name, String channelLogo) {
        this.id = id;
        this.name = name;
        this.channelLogo = channelLogo;
    }
}
