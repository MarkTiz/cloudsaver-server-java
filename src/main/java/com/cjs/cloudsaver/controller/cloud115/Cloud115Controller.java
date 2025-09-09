package com.cjs.cloudsaver.controller.cloud115;

import com.alibaba.fastjson2.JSONObject;
import com.cjs.cloudsaver.common.exception.BizException;
import com.cjs.cloudsaver.config.security.AppUserDetails;
import com.cjs.cloudsaver.model.cloud115.Cloud115File;
import com.cjs.cloudsaver.model.cloud115.Cloud115Folder;
import com.cjs.cloudsaver.model.cloud115.cloud115OfflineDownload;
import com.cjs.cloudsaver.model.cloud115.vo.Cloud115SaveFileVo;
import com.cjs.cloudsaver.service.cloud115.Cloud115Service;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class Cloud115Controller {


    @Value("${115config.root_username}")
    private String cloud115RootUsername;

    @Resource
    private Cloud115Service cloud115Service;



    /**
     * 解析115分享链接
     */
    @GetMapping("/cloud115/share-info")
    public List<Cloud115File> getShareInfo(
            @AuthenticationPrincipal AppUserDetails appUserDetails,
            @RequestParam("shareCode") String shareCode,
            @RequestParam(value = "receiveCode", defaultValue = "") String receiveCode) throws BizException {
        return cloud115Service.getShareInfo(shareCode, receiveCode, appUserDetails.getUserId());

    }

    /**
     * 获取115文件夹列表
     */
    @GetMapping("/cloud115/folders")
    public List<Cloud115Folder> getfolders(
            @AuthenticationPrincipal AppUserDetails appUserDetails,
            @RequestParam(value = "parentCid", defaultValue = "0") String parentCid) throws BizException {
        return cloud115Service.getFolderList(parentCid, appUserDetails.getUserId());

    }

    /**
     * 获取管理员 115文件夹列表
     */
    @GetMapping("/cloud115/admin-folders")
    public List<Cloud115Folder> getAdminfolders(
            @AuthenticationPrincipal AppUserDetails appUserDetails,
            @RequestParam("parentCid") String parentCid) throws BizException {
        if (appUserDetails.getAccountUser().getUsername().equals(cloud115RootUsername)) {
            throw new BizException("权限不足");
        }
        return cloud115Service.getAdminFolderList(parentCid, appUserDetails.getUserId());

    }

    /**
     * 转存文件
     */
    @PostMapping("/cloud115/save")
    public JSONObject save(
            @AuthenticationPrincipal AppUserDetails appUserDetails,
            @RequestBody Cloud115SaveFileVo cloud115SaveFileVo) throws BizException {
        return cloud115Service.saveSharedFile(cloud115SaveFileVo.getShareCode(), cloud115SaveFileVo.getReceiveCode(), cloud115SaveFileVo.getFileId(), cloud115SaveFileVo.getFolderId(), appUserDetails.getUserId());
    }

    /**
     * 离线下载
     */
    @PostMapping("/cloud115/offline-download")
    public void offlineDownload(
            @AuthenticationPrincipal AppUserDetails appUserDetails,
            @RequestBody String url,
            @RequestBody String savePath,
            @RequestBody String sign,
            @RequestBody String time,
            @RequestBody String uid) throws BizException {
        cloud115Service.addOfflineDownload(url, savePath, sign, time, uid, appUserDetails.getUserId());
    }

    /**
     * 获取离线参数
     */
    @GetMapping("/cloud115/offline-params")
    public cloud115OfflineDownload getAdminfolders(
            @AuthenticationPrincipal AppUserDetails appUserDetails) throws BizException {
        return cloud115Service.getOfflineDownloadParams(appUserDetails.getUserId());

    }


}
