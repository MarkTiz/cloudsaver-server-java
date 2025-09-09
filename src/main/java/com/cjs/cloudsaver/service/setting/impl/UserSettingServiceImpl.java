package com.cjs.cloudsaver.service.setting.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cjs.cloudsaver.mapper.setting.UserSettingMapper;
import com.cjs.cloudsaver.model.account.AccountUser;
import com.cjs.cloudsaver.model.setting.GlobalSetting;
import com.cjs.cloudsaver.model.setting.UserSetting;
import com.cjs.cloudsaver.model.setting.vo.SettingVo;
import com.cjs.cloudsaver.service.common.impl.BaseServiceImpl;
import com.cjs.cloudsaver.service.setting.GlobalSettingService;
import com.cjs.cloudsaver.service.setting.UserSettingService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class UserSettingServiceImpl extends BaseServiceImpl<UserSettingMapper, UserSetting> implements UserSettingService {

    @Resource
    private GlobalSettingService globalSettingService;

    @Override
    public SettingVo findByUserId(String userId) {
        SettingVo settingVo = new SettingVo();
        LambdaQueryWrapper<UserSetting> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(UserSetting::getUserId, userId);
        UserSetting userSetting = this.getBaseMapper().selectOne(queryWrapper);
        GlobalSetting globalSetting = globalSettingService.findOne();

        settingVo.setUserSetting(userSetting);
        settingVo.setGlobalSetting(globalSetting);
        return settingVo;
    }

    @Override
    public void saveByUserId(SettingVo settingVo, AccountUser accountUser) {
        UserSetting userSetting = settingVo.getUserSetting();
        this.updateByUserId(userSetting,accountUser.getUserId());
        GlobalSetting globalSetting = settingVo.getGlobalSetting();
        Integer role = accountUser.getRole();
        if (role == 1 && globalSetting != null) {
            globalSettingService.update(globalSetting);
        }

    }

    private void updateByUserId(UserSetting userSetting,String userId){
        UpdateWrapper<UserSetting> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("userId", userId);

        this.getBaseMapper().update(userSetting, updateWrapper);
    }
}
