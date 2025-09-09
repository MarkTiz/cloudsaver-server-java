package com.cjs.cloudsaver.controller.account;


import com.cjs.cloudsaver.common.exception.BizException;
import com.cjs.cloudsaver.config.security.AppUserDetails;
import com.cjs.cloudsaver.model.account.AccountUser;
import com.cjs.cloudsaver.model.account.vo.AccountUserVo;
import com.cjs.cloudsaver.service.account.AccountUserService;
import com.cjs.cloudsaver.utils.PasswordUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountUserController {

    private final AccountUserService accountUserService;

    private final PasswordUtils passwordUtils;


    public AccountUserController(AccountUserService accountUserService, PasswordUtils passwordUtils) {
        this.accountUserService = accountUserService;
        this.passwordUtils = passwordUtils;
    }


    /**
     * 创建账户用户
     *
     * @throws BizException 如果业务逻辑处理中出现异常
     */
    @PostMapping("/user/register")
    public void create(@RequestBody AccountUserVo accountUserVo) throws BizException {
        accountUserService.createByVo(accountUserVo, passwordUtils);
    }


    /**
     * 获取当前登录用户的账户信息
     *
     * @param appUserDetails 当前登录用户的信息
     * @return 用户的账户信息
     */
    @GetMapping("/accountUser/info")
    public AccountUser info(@AuthenticationPrincipal AppUserDetails appUserDetails) {
        return accountUserService.findById(appUserDetails.getAccountUser().getId());
    }


}
