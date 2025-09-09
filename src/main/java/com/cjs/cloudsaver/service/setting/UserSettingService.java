package com.cjs.cloudsaver.service.setting;

import com.cjs.cloudsaver.model.account.AccountUser;
import com.cjs.cloudsaver.model.setting.UserSetting;
import com.cjs.cloudsaver.model.setting.vo.SettingVo;
import com.cjs.cloudsaver.service.common.BaseService;

public interface UserSettingService extends BaseService<UserSetting> {

    SettingVo findByUserId(String userId);

    void saveByUserId(SettingVo settingVo, AccountUser user);

}
