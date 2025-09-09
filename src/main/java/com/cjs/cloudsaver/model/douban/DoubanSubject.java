package com.cjs.cloudsaver.model.douban;

import lombok.Data;

@Data
public class DoubanSubject {
    private String id;
    private String title;
    private String rate;
    private String cover;
    private String url;
    private boolean isNew;
}
