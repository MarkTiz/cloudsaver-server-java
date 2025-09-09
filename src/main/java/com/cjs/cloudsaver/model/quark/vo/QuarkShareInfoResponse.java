package com.cjs.cloudsaver.model.quark.vo;

import com.cjs.cloudsaver.model.quark.QuarkFile;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class QuarkShareInfoResponse {

    @JsonProperty("list")
    private List<QuarkFile> quarkFileList;

    private String pwdId;

    private String stoken;

    private long fileSize;
}
