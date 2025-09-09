package com.cjs.cloudsaver.service.setting.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cjs.cloudsaver.mapper.setting.GlobalSettingMapper;
import com.cjs.cloudsaver.model.setting.GlobalSetting;
import com.cjs.cloudsaver.service.common.impl.BaseServiceImpl;
import com.cjs.cloudsaver.service.setting.GlobalSettingService;
import org.springframework.stereotype.Service;

@Service
public class GlobalSettingServiceImpl  extends BaseServiceImpl<GlobalSettingMapper, GlobalSetting> implements GlobalSettingService {
    @Override
    public GlobalSetting findOne() {
        LambdaQueryWrapper<GlobalSetting> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.last("LIMIT 1");
        return this.getBaseMapper().selectOne(queryWrapper);
    }
}
