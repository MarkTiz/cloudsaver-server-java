package com.cjs.cloudsaver.model.account.vo;

import com.cjs.cloudsaver.model.account.AccountUser;
import lombok.Getter;
import lombok.Setter;

/**
 * 后台用户创建 Vo
 */
@Getter
@Setter
public class AccountUserVo extends AccountUser {

    private String registerCode;


}
