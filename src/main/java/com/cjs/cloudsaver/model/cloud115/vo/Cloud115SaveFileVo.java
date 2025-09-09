package com.cjs.cloudsaver.model.cloud115.vo;

import lombok.Data;

@Data
public class Cloud115SaveFileVo {


    /**
     * 分享码
     */
    private String shareCode;

    /**
     * 接收码
     */
    private String receiveCode;

    /**
     * 文件id
     */
    private String fileId;

    /**
     * 文件夹id
     */
    private String folderId;
}
