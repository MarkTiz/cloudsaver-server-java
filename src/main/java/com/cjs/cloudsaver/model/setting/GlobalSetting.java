package com.cjs.cloudsaver.model.setting;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cjs.cloudsaver.model.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
@TableName("global_settings")
public class GlobalSetting extends BaseEntity {


    /**
     * 代理ip
     */
    private String httpProxyHost;


    /**
     * 代理端口
     */
    private String httpProxyPort;


    /**
     * 是否开启代理
     */
    private Boolean isProxyEnabled;

    /**
     * 普通用户注册code
     */
    @JsonProperty("CommonUserCode")
    private String commonUserCode;

    /**
     * 管理员注册code
     */
    @JsonProperty("AdminUserCode")
    private String adminUserCode;


}
