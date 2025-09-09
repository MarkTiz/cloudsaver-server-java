package com.cjs.cloudsaver.model.cloud115;

import lombok.Data;

@Data
public class Cloud115File {
    /**
     * 文件id
     */
    private String fileId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件大小(kb)
     */
    private long fileSize;

}
