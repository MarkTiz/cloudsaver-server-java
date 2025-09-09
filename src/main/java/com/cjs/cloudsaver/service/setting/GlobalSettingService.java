package com.cjs.cloudsaver.service.setting;

import com.cjs.cloudsaver.model.setting.GlobalSetting;
import com.cjs.cloudsaver.service.common.BaseService;

public interface GlobalSettingService  extends BaseService<GlobalSetting> {

    /**
     * 全局数据,仅有一条
     * @return
     */
    GlobalSetting findOne();
}
