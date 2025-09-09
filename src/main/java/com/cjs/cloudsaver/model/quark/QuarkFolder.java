package com.cjs.cloudsaver.model.quark;

import lombok.Data;

import java.util.List;

// 文件夹信息模型
@Data
public class QuarkFolder{
    private String cid;
    private String name;
    private List<QuarkPathItem> path;
}
