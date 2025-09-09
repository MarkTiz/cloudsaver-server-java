package com.cjs.cloudsaver.service.account;


import com.cjs.cloudsaver.common.exception.BizException;
import com.cjs.cloudsaver.model.account.AccountUser;
import com.cjs.cloudsaver.model.account.vo.AccountUserVo;
import com.cjs.cloudsaver.service.common.BaseService;
import com.cjs.cloudsaver.utils.PasswordUtils;

public interface AccountUserService extends BaseService<AccountUser> {


    /**
     * 根据登录名查询后台用户
     *
     * @param loginName 登录名
     * @return 用户
     */
    AccountUser findByLoginName(String loginName);

    /**
     * 根据登录名查询后台用户
     *
     * @param userId 登录名
     * @return 用户
     */
    AccountUser findByUserId(String userId);

    /**
     * 创建用户
     *
     * @param accountUser 用户信息
     */
    AccountUser createByVo(AccountUserVo accountUser, PasswordUtils passwordUtils) throws BizException;

    /**
     * 删除用户
     *
     * @param accountUser 用户
     */
    void deletedByVo(AccountUser accountUser, String operator) throws BizException;


    AccountUser update(AccountUserVo accountUserVo, String operator, PasswordUtils passwordUtils) throws BizException;

}
