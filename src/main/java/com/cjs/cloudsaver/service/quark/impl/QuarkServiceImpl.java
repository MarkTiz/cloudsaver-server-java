package com.cjs.cloudsaver.service.quark.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import com.cjs.cloudsaver.common.exception.BizException;
import com.cjs.cloudsaver.model.quark.QuarkFile;
import com.cjs.cloudsaver.model.quark.QuarkFolder;
import com.cjs.cloudsaver.model.quark.vo.QuarkSaveFileVo;
import com.cjs.cloudsaver.model.quark.vo.QuarkShareInfoResponse;
import com.cjs.cloudsaver.service.quark.QuarkService;
import com.cjs.cloudsaver.service.setting.UserSettingService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class QuarkServiceImpl implements QuarkService {

    @Value("${quarkconfig.web_api}")
    private String BASE_URL;

    private final OkHttpClient client;

    @Value("${quarkconfig.root_folder_id}")
    private String quarkParentCid;

    @Resource
    private UserSettingService userSettingService;

    public QuarkServiceImpl() {
        this.client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .header("Accept", "application/json, text/plain, */*")
                            .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
                            .header("Content-Type", "application/json")
                            .header("Priority", "u=1, i")
                            .header("Sec-Ch-Ua", "\"Microsoft Edge\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"")
                            .header("Sec-Ch-Ua-Mobile", "?0")
                            .header("Sec-Ch-Ua-Platform", "\"Windows\"")
                            .header("Sec-Fetch-Dest", "empty")
                            .header("Sec-Fetch-Mode", "cors")
                            .header("Sec-Fetch-Site", "same-site")
                            .build();
                    return chain.proceed(request);
                })
                .build();
    }

    @Override
    public QuarkShareInfoResponse getShareInfo(String pwdId, String passcode, String userId) throws BizException {
        String quarkCookie = userSettingService.findByUserId(userId).getUserSetting().getQuarkCookie();
        if (quarkCookie == null || quarkCookie.isEmpty()) {
            throw new BizException("quark_cookie_not_exist");
        }

        // 获取 stoken
        String tokenUrl = BASE_URL + "/1/clouddrive/share/sharepage/token?pr=ucpro&fr=pc&uc_param_str=&__dt=994&__t=" + System.currentTimeMillis();
        RequestBody tokenBody = RequestBody.create(
                JSON.toJSONString(new JSONObject()
                        .fluentPut("pwd_id", pwdId)
                        .fluentPut("passcode", passcode != null ? passcode : "")),
                MediaType.parse("application/json")
        );
        Request tokenRequest = new Request.Builder()
                .url(tokenUrl)
                .post(tokenBody)
                .addHeader("Cookie", quarkCookie)
                .build();

        try (Response tokenResponse = client.newCall(tokenRequest).execute()) {
            if (!tokenResponse.isSuccessful()) {
                String errorBody = tokenResponse.body() != null ? tokenResponse.body().string() : "无响应体";
                throw new BizException("quark_stoken_fail: ", errorBody);
            }

            String tokenResponseBody = tokenResponse.body().string();

            JSONObject tokenJson = JSON.parseObject(tokenResponseBody);
            if (tokenJson.getInteger("status") == 200 && tokenJson.containsKey("data")) {
                String stoken = tokenJson.getJSONObject("data").getString("stoken");
                if (stoken != null) {
                    return getShareList(pwdId, stoken, quarkCookie);
                }
                throw new BizException("quark_stoken_fail", "");
            } else {
                throw new BizException("quark_stoken_fail", tokenJson.getString("message"));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private QuarkShareInfoResponse getShareList(String pwdId, String stoken, String quarkCookie) throws BizException {
        QuarkShareInfoResponse quarkShareInfoResponse = new QuarkShareInfoResponse();
        HttpUrl url = HttpUrl.parse(BASE_URL + "/1/clouddrive/share/sharepage/detail").newBuilder()
                .addQueryParameter("pr", "ucpro")
                .addQueryParameter("fr", "pc")
                .addQueryParameter("uc_param_str", "")
                .addQueryParameter("pwd_id", pwdId)
                .addQueryParameter("stoken", stoken)
                .addQueryParameter("pdir_fid", "0")
                .addQueryParameter("force", "0")
                .addQueryParameter("_page", "1")
                .addQueryParameter("_size", "50")
                .addQueryParameter("_fetch_banner", "1")
                .addQueryParameter("_fetch_share", "1")
                .addQueryParameter("_fetch_total", "1")
                .addQueryParameter("_sort", "file_type:asc,updated_at:desc")
                .addQueryParameter("__dt", "1589")
                .addQueryParameter("__t", String.valueOf(System.currentTimeMillis()))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Cookie", quarkCookie)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "无响应体";
                throw new BizException("api_not_complete", errorBody);
            }

            String responseBody = response.body().string();

            JSONObject json = JSON.parseObject(responseBody);
            if (json.containsKey("data")) {
                JSONObject data = json.getJSONObject("data");
                List<JSONObject> list = data.getList("list", JSONObject.class);
                JSONObject share = data.getJSONObject("share");
                JSONObject QuarkFiles = data.getJSONObject("file_infos");
                long shareSize = share != null ? share.getLongValue("size") : 0;

                List<QuarkFile> quarkFileList = new ArrayList<>();
                if (list != null && !list.isEmpty()) {
                    for (JSONObject item : list) {
                        if (item.containsKey("fid")) {
                            QuarkFile quarkFile = new QuarkFile();
                            quarkFile.setFileId(item.getString("fid"));
                            quarkFile.setFileName(item.getString("file_name"));
                            quarkFile.setFileIdToken(item.getString("share_fid_token"));
                            quarkFile.setFileSize(QuarkFiles != null && QuarkFiles.containsKey(item.getString("fid"))
                                    ? QuarkFiles.getJSONObject(item.getString("fid")).getLongValue("size")
                                    : 0);
                            quarkFileList.add(quarkFile);
                        }
                    }
                }
                quarkShareInfoResponse.setQuarkFileList(quarkFileList);
                quarkShareInfoResponse.setPwdId(pwdId);
                quarkShareInfoResponse.setStoken(stoken);
                quarkShareInfoResponse.setFileSize(shareSize);
                return quarkShareInfoResponse;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public List<QuarkFolder> getFolderList(String parentCid, String userId) throws BizException {
        if ("0".equals(parentCid)) {
            parentCid = quarkParentCid;
        }

        String quarkCookie = userSettingService.findByUserId(userId).getUserSetting().getQuarkCookie();
        if (quarkCookie == null || quarkCookie.isEmpty()) {
            throw new BizException("quark_cookie_not_exist");
        }

        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(BASE_URL + "/1/clouddrive/file/sort")).newBuilder()
                .addQueryParameter("pr", "ucpro")
                .addQueryParameter("fr", "pc")
                .addQueryParameter("uc_param_str", "")
                .addQueryParameter("pdir_fid", parentCid)
                .addQueryParameter("_page", "1")
                .addQueryParameter("_size", "100")
                .addQueryParameter("_fetch_total", "false")
                .addQueryParameter("_fetch_sub_dirs", "1")
                .addQueryParameter("_sort", "")
                .addQueryParameter("__dt", "2093126")
                .addQueryParameter("__t", String.valueOf(System.currentTimeMillis()))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Cookie", quarkCookie)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "无响应体";
                throw new BizException("api_not_complete" ,errorBody);
            }

            String responseBody = response.body().string();

            try {
                JSONObject json = JSON.parseObject(responseBody);
                if (json.containsKey("data") && json.getJSONObject("data").containsKey("list")) {
                    List<JSONObject> list = json.getJSONObject("data").getList("list", JSONObject.class);
                    List<QuarkFolder> folderInfoList = new ArrayList<>();
                    if (list != null) {
                        for (JSONObject item : list) {
                            if (item.containsKey("fid") && item.getInteger("file_type") == 0) {
                                QuarkFolder folderInfo = new QuarkFolder();
                                folderInfo.setCid(item.getString("fid"));
                                folderInfo.setName(item.getString("file_name"));
                                folderInfo.setPath(new ArrayList<>());
                                folderInfoList.add(folderInfo);
                            }
                        }
                    }
                    return folderInfoList;
                } else {
                    throw new BizException("api_not_complete" , json.getString("message"));
                }
            } catch (JSONException e) {
                throw new BizException("api_not_complete",responseBody);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JSONObject saveSharedFile(QuarkSaveFileVo quarkSaveFileVo, String userId) throws BizException {
        String quarkCookie = userSettingService.findByUserId(userId).getUserSetting().getQuarkCookie();
        if (quarkCookie == null || quarkCookie.isEmpty()) {
            throw new BizException("quark_cookie_not_exist");
        }

        String saveUrl = BASE_URL + "/1/clouddrive/share/sharepage/save?pr=ucpro&fr=pc&uc_param_str=&__dt=208097&__t=" + System.currentTimeMillis();
        RequestBody requestBody = RequestBody.create(
                JSON.toJSONString(quarkSaveFileVo),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(saveUrl)
                .post(requestBody)
                .addHeader("Cookie", quarkCookie)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "无响应体";
                throw new BizException("api_not_complete" , errorBody);
            }
            String responseBody = response.body().string();
            JSONObject json = JSON.parseObject(responseBody);
            JSONObject dataObj = json.getJSONObject("data");
            return dataObj.getJSONObject("data");


        } catch (IOException e) {
            throw new BizException("api_not_complete", e.getMessage());
        }
    }
}
