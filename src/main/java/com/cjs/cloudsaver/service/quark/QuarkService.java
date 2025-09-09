package com.cjs.cloudsaver.service.quark;

import com.alibaba.fastjson2.JSONObject;
import com.cjs.cloudsaver.common.exception.BizException;
import com.cjs.cloudsaver.model.quark.QuarkFolder;
import com.cjs.cloudsaver.model.quark.vo.QuarkSaveFileVo;
import com.cjs.cloudsaver.model.quark.vo.QuarkShareInfoResponse;

import java.util.List;

public interface QuarkService {
    QuarkShareInfoResponse getShareInfo(String pwdId, String passcode, String userId) throws BizException;

    List<QuarkFolder> getFolderList(String parentCid, String userId) throws BizException;

    JSONObject saveSharedFile(QuarkSaveFileVo quarkSaveFileVo, String userId) throws BizException;
}
