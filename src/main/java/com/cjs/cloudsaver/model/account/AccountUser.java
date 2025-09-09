package com.cjs.cloudsaver.model.account;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cjs.cloudsaver.model.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
@TableName("users")
public class AccountUser extends BaseEntity {


    /**
     * 用户唯一id
     */
    private String userId;


    /**
     * 登录名
     */
    private String username;



    /**
     * 密码哈希值
     */
    @JsonIgnore
    private String password;



    /**
     * 是否管理员
     */
    private Integer role;




}
