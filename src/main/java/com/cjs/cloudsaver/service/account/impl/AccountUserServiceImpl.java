package com.cjs.cloudsaver.service.account.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cjs.cloudsaver.common.exception.BizException;
import com.cjs.cloudsaver.mapper.account.AccountUserMapper;
import com.cjs.cloudsaver.model.account.AccountUser;
import com.cjs.cloudsaver.model.account.vo.AccountUserVo;
import com.cjs.cloudsaver.model.setting.GlobalSetting;
import com.cjs.cloudsaver.service.account.AccountUserService;
import com.cjs.cloudsaver.service.common.impl.BaseServiceImpl;
import com.cjs.cloudsaver.service.setting.GlobalSettingService;
import com.cjs.cloudsaver.utils.PasswordUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class AccountUserServiceImpl extends BaseServiceImpl<AccountUserMapper, AccountUser> implements AccountUserService {

    @Resource
    private GlobalSettingService globalSettingService;

    @Override
    public AccountUser findByLoginName(String username) {
        LambdaQueryWrapper<AccountUser> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(AccountUser::getUsername, username);
        return this.getBaseMapper().selectOne(queryWrapper);
    }

    @Override
    public AccountUser findByUserId(String userId) {
        LambdaQueryWrapper<AccountUser> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(AccountUser::getUserId, userId);
        return this.getBaseMapper().selectOne(queryWrapper);
    }

    @Override
    public AccountUser createByVo(AccountUserVo accountUserVo, PasswordUtils passwordUtils) throws BizException {
        String registerCode = accountUserVo.getRegisterCode();
        GlobalSetting globalSetting = globalSettingService.findOne();
        String commonUserCode = globalSetting.getCommonUserCode();
        String adminUserCode = globalSetting.getAdminUserCode();
        int role ;
        if (commonUserCode.equals(registerCode)){
            role =0;
        }else if (adminUserCode.equals(registerCode)){
            role =1;
        }else {
            throw new BizException("register_code_no_exist");
        }
        AccountUser user = new AccountUser();
        user.setUserId(UUID.randomUUID().toString());
        user.setUsername(accountUserVo.getUsername());
        user.setPassword(passwordUtils.encode(accountUserVo.getPassword()));
        user.setRole(role);
        checkCreate(user);
        return this.create(user);
    }

    @Override
    public void deletedByVo(AccountUser accountUser, String operator) throws BizException {
        AccountUser dbData = this.findById(accountUser.getId());
        if (dbData == null) {
            throw new BizException("account_user_no_exist");
        }
        this.delete(accountUser);
        //强制用户退出登录
    }


    @Override
    public AccountUser update(AccountUserVo accountUserVo, String operator, PasswordUtils passwordUtils) throws BizException {
        AccountUser dbData = this.findById(accountUserVo.getId());
        if (dbData == null) {
            throw new BizException("account_user_no_exist");
        }
        AccountUser user = new AccountUser();
        BeanUtils.copyProperties(accountUserVo, user);
        user.setUserId(null); //用户id唯一 不允许修改
        if (accountUserVo.getPassword() != null) {
            user.setPassword(passwordUtils.encode(accountUserVo.getPassword()));
        }
        return this.update(user);
    }


    private void checkCreate(AccountUser user) throws BizException {
        LambdaQueryWrapper<AccountUser> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(AccountUser::getUsername, user.getUsername());
        AccountUser dbData = this.getBaseMapper().selectOne(queryWrapper);
        if (dbData != null) {
            throw new BizException("account_user_exist");
        }
    }


}
