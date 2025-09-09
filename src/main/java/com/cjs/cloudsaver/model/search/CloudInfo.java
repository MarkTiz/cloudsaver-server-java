package com.cjs.cloudsaver.model.search;

import lombok.Data;

import java.util.List;

@Data
public class CloudInfo {
    private List<String> links;
    private String cloudType;

    public CloudInfo(List<String> links, String cloudType) {
        this.links = links;
        this.cloudType = cloudType;
    }
}
