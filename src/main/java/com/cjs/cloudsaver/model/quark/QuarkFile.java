package com.cjs.cloudsaver.model.quark;

import lombok.Data;

@Data
public class QuarkFile {
    private String fileId;
    private String fileName;
    private long fileSize;
    private String fileIdToken; // 夸克网盘特有字段
}
