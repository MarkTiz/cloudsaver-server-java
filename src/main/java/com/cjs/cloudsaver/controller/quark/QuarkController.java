package com.cjs.cloudsaver.controller.quark;

import com.alibaba.fastjson2.JSONObject;
import com.cjs.cloudsaver.common.exception.BizException;
import com.cjs.cloudsaver.config.security.AppUserDetails;
import com.cjs.cloudsaver.model.quark.QuarkFolder;
import com.cjs.cloudsaver.model.quark.vo.QuarkSaveFileVo;
import com.cjs.cloudsaver.model.quark.vo.QuarkShareInfoResponse;
import com.cjs.cloudsaver.service.quark.QuarkService;
import jakarta.annotation.Resource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class QuarkController {

    @Resource
    private QuarkService quarkService;

    /**
     * 解析夸克分享链接
     */
    @GetMapping("/quark/share-info")
    public QuarkShareInfoResponse getShareInfo(
            @AuthenticationPrincipal AppUserDetails appUserDetails,
            @RequestParam("pwdId") String pwdId,
            @RequestParam(value = "passcode", defaultValue = "") String passcode) throws BizException {
        return quarkService.getShareInfo(pwdId, passcode, appUserDetails.getUserId());

    }


    /**
     * 获取夸克文件夹列表
     */
    @GetMapping("/quark/folders")
    public List<QuarkFolder> getfolders(
            @AuthenticationPrincipal AppUserDetails appUserDetails,
            @RequestParam(value = "parentCid", defaultValue = "0") String parentCid) throws BizException {
        return quarkService.getFolderList(parentCid, appUserDetails.getUserId());

    }

    /**
     * 转存文件
     */
    @PostMapping("/quark/save")
    public JSONObject save(
            @AuthenticationPrincipal AppUserDetails appUserDetails,
            @RequestBody QuarkSaveFileVo quarkSaveFileVo) throws BizException {

        return quarkService.saveSharedFile(quarkSaveFileVo, appUserDetails.getUserId());
    }

}
