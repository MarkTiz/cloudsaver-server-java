package com.cjs.cloudsaver.model.cloud115;

import lombok.Data;

import java.util.List;

@Data
public class Cloud115Folder {
    /**
     * 文件夹ID
     */
    private String cid;

    /**
     * 文件夹名称
     */
    private String name;

    /**
     * 文件夹路径
     */
    private List<Cloud115PathItem> path;

}
