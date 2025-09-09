package com.cjs.cloudsaver.model.setting;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cjs.cloudsaver.model.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_settings")
public class UserSetting extends BaseEntity {


    /**
     * 用户唯一id
     */
    private String userId;


    /**
     * 115用户名
     */
    private String cloud115UserId;



    /**
     * 115Cookie
     */
    @JsonIgnore
    private String cloud115Cookie;

    /**
     * 夸克Cookie
     */
    @JsonIgnore
    private String quarkCookie;







}
