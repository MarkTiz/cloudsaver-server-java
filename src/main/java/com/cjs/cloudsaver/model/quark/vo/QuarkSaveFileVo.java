package com.cjs.cloudsaver.model.quark.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class QuarkSaveFileVo {

    @JsonProperty("fid_list")
    private List<String> fidList;
    @JsonProperty("fid_token_list")
    private List<String> fidTokenList;
    @JsonProperty("to_pdir_fid")
    private String toPdirFid;
    @JsonProperty("pwd_id")
    private String pwdId;
    private String stoken;
    @JsonProperty("pdir_fid")
    private String pdirFid;
    private String scene;
}
