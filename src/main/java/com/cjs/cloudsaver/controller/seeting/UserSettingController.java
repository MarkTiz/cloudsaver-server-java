package com.cjs.cloudsaver.controller.seeting;


import com.cjs.cloudsaver.config.security.AppUserDetails;
import com.cjs.cloudsaver.model.setting.vo.SettingVo;
import com.cjs.cloudsaver.service.setting.UserSettingService;
import jakarta.annotation.Resource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserSettingController {


    @Resource
    private  UserSettingService userSettingService;


    /**
     * 获取当前登录用户的设置信息
     *
     * @param appUserDetails 当前登录用户的信息
     * @return 用户的账户信息
     */
    @GetMapping("/setting/get")
    public SettingVo getSetting(@AuthenticationPrincipal AppUserDetails appUserDetails) {
        return userSettingService.findByUserId(appUserDetails.getAccountUser().getUserId());
    }


    /**
     * 获取当前登录用户的设置信息
     *
     * @param appUserDetails 当前登录用户的信息
     * @return 用户的账户信息
     */
    @PostMapping("/setting/save")
    public void saveSetting(@RequestBody SettingVo settingVo, @AuthenticationPrincipal AppUserDetails appUserDetails) {
         userSettingService.saveByUserId(settingVo,appUserDetails.getAccountUser());
    }


}
