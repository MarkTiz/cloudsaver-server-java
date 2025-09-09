package com.cjs.cloudsaver.model.setting.vo;

import com.cjs.cloudsaver.model.setting.GlobalSetting;
import com.cjs.cloudsaver.model.setting.UserSetting;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SettingVo {
    @JsonProperty("userSettings")
    private UserSetting userSetting;

    @JsonProperty("globalSetting")
    private GlobalSetting globalSetting;
}
