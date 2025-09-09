package com.cjs.cloudsaver.service.cloud115;

import com.alibaba.fastjson2.JSONObject;
import com.cjs.cloudsaver.common.exception.BizException;
import com.cjs.cloudsaver.model.cloud115.Cloud115File;
import com.cjs.cloudsaver.model.cloud115.Cloud115Folder;
import com.cjs.cloudsaver.model.cloud115.cloud115OfflineDownload;

import java.util.List;

// 分享信息服务接口
public interface Cloud115Service {
    List<Cloud115File> getShareInfo(String shareCode, String receiveCode, String userId) throws BizException;

    List<Cloud115Folder> getFolderList(String parentCid, String userId) throws BizException;

    List<Cloud115Folder> getAdminFolderList(String parentCid, String userId) throws BizException;
    JSONObject saveSharedFile(String shareCode, String receiveCode, String fileId, String cid, String userId) throws BizException;


    void addOfflineDownload(String url, String savePath, String sign, String time, String uid,String userId) throws BizException;

    cloud115OfflineDownload getOfflineDownloadParams(String userId) throws BizException;

}
