package com.cjs.cloudsaver.service.cloud115.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cjs.cloudsaver.common.exception.BizException;
import com.cjs.cloudsaver.model.cloud115.Cloud115File;
import com.cjs.cloudsaver.model.cloud115.Cloud115Folder;
import com.cjs.cloudsaver.model.cloud115.cloud115OfflineDownload;
import com.cjs.cloudsaver.model.cloud115.Cloud115PathItem;
import com.cjs.cloudsaver.service.cloud115.Cloud115Service;
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
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class Cloud115ServiceImpl implements Cloud115Service {


    @Value("${115config.web_api}")
    private String LIXIAN_BASE_URL;

    @Value("${115config.offline_api}")
    private String WEB_API_BASE_URL;

    @Value("${115config.sign}")
    private String sign;

    @Value("${115config.uid}")
    private String uid;

    @Value("${115config.root_folder_id}")
    private String cloud115ParentCid;

    //api client
    private final OkHttpClient webApiClient;

    //离线client
    private final OkHttpClient lixianClient;

    @Resource
    private UserSettingService userSettingService;

    public Cloud115ServiceImpl() {
        this.webApiClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .header("Host", "webapi.115.com")
                            .header("Connection", "keep-alive")
                            .header("xweb_xhr", "1")
                            .header("Origin", "")
                            .header("Content-Type", "application/x-www-form-urlencoded")
                            .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36 MicroMessenger/6.8.0(0x16080000) NetType/WIFI MiniProgramEnv/Mac MacWechat/WMPF MacWechat/3.8.9(0x13080910) XWEB/1227")
                            .header("Accept", "*/*")
                            .header("Sec-Fetch-Site", "cross-site")
                            .header("Sec-Fetch-Mode", "cors")
                            .header("Sec-Fetch-Dest", "empty")
                            .header("Referer", "https://servicewechat.com/wx2c744c010a61b0fa/94/page-frame.html")
                            .header("Accept-Encoding", " deflate, br")
                            .header("Accept-Language", "zh-CN,zh;q=0.9")
                            .build();
                    return chain.proceed(request);
                })
                .build();

        this.lixianClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .header("Accept", "application/json, text/javascript, */*; q=0.01")
                            .header("Origin", "https://115.com")
                            .header("X-Requested-With", "XMLHttpRequest")
                            .header("Content-Type", "application/x-www-form-urlencoded")
                            .build();
                    return chain.proceed(request);
                })
                .build();
    }

    @Override
    public List<Cloud115File> getShareInfo(String shareCode, String receiveCode, String userId) throws BizException {
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(WEB_API_BASE_URL + "/share/snap")).newBuilder()
                .addQueryParameter("share_code", shareCode)
                .addQueryParameter("receive_code", receiveCode != null ? receiveCode : "")
                .addQueryParameter("offset", "0")
                .addQueryParameter("limit", "20")
                .addQueryParameter("cid", "")
                .build();

        String cloud115Cookie = userSettingService.findByUserId(userId).getUserSetting().getCloud115Cookie();
        if (cloud115Cookie == null || cloud115Cookie.isEmpty()) {
            throw new BizException("115cloud_cookie_not_exist");
        }

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Cookie", cloud115Cookie)
                .build();

        try (Response response = webApiClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new BizException("api_not_complete ",response.code());
            }

            assert response.body() != null;
            String responseBody = response.body().string();
            JSONObject json = JSON.parseObject(responseBody);

            if (json.getBoolean("state") && json.containsKey("data")) {
                JSONObject data = json.getJSONObject("data");
                List<JSONObject> list = data.getList("list", JSONObject.class);
                if (list != null && !list.isEmpty()) {
                    List<Cloud115File> cloud115Files = new ArrayList<>();
                    for (JSONObject item : list) {
                        Cloud115File cloud115File = new Cloud115File();
                        cloud115File.setFileId(item.getString("cid"));
                        cloud115File.setFileName(item.getString("n"));
                        cloud115File.setFileSize(item.getLongValue("s"));
                        cloud115Files.add(cloud115File);
                    }
                    return cloud115Files;
                }
            }
            throw new BizException("file_message_no_find");
        } catch (Exception e) {
            throw new BizException("api_not_complete", e.getMessage());
        }
    }

    @Override
    public List<Cloud115Folder> getFolderList(String parentCid, String userId) throws BizException {
        if (parentCid.equals("0")) {
            parentCid = cloud115ParentCid;
        }
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(WEB_API_BASE_URL + "/files")).newBuilder()
                .addQueryParameter("aid", "1")
                .addQueryParameter("cid", parentCid != null ? parentCid : "0")
                .addQueryParameter("o", "user_ptime")
                .addQueryParameter("asc", "1")
                .addQueryParameter("offset", "0")
                .addQueryParameter("show_dir", "1")
                .addQueryParameter("limit", "50")
                .addQueryParameter("type", "0")
                .addQueryParameter("format", "json")
                .addQueryParameter("star", "0")
                .addQueryParameter("suffix", "")
                .addQueryParameter("natsort", "0")
                .addQueryParameter("snap", "0")
                .addQueryParameter("record_open_time", "1")
                .addQueryParameter("fc_mix", "0")
                .build();

        String cloud115Cookie = userSettingService.findByUserId(userId).getUserSetting().getCloud115Cookie();
        if (cloud115Cookie == null || cloud115Cookie.isEmpty()) {
            throw new BizException("115cloud_cookie_not_exist");
        }

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Cookie", cloud115Cookie)
                .build();

        try (Response response = webApiClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new BizException("api_not_complete",response.code());
            }

            assert response.body() != null;
            String responseBody = response.body().string();
            JSONObject json = JSON.parseObject(responseBody);

            if (json.getBoolean("state")) {
                List<JSONObject> data = json.getList("data", JSONObject.class);
                List<JSONObject> path = json.getList("path", JSONObject.class);
                List<Cloud115Folder> cloud115Folders = new ArrayList<>();
                for (JSONObject item : data) {
                    if (item.containsKey("cid") && item.containsKey("ns")) {
                        Cloud115Folder cloud115Folder = new Cloud115Folder();
                        cloud115Folder.setCid(item.getString("cid"));
                        cloud115Folder.setName(item.getString("n"));
                        List<Cloud115PathItem> cloud115PathItems = new ArrayList<>();
                        if (path != null) {
                            for (JSONObject pathItem : path) {
                                Cloud115PathItem pi = new Cloud115PathItem();
                                pi.setCid(pathItem.getString("cid"));
                                pi.setName(pathItem.getString("name"));
                                cloud115PathItems.add(pi);
                            }
                        }
                        cloud115Folder.setPath(cloud115PathItems);
                        cloud115Folders.add(cloud115Folder);
                    }
                }
                return cloud115Folders;
            } else {
                throw new BizException("api_not_complete", json.getString("error"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Cloud115Folder> getAdminFolderList(String parentCid, String userId) throws BizException {
        return getFolderList(parentCid, userId);
    }

    @Override
    public JSONObject saveSharedFile(String shareCode, String receiveCode, String fileId, String cid, String userId) throws BizException {
        String cloud115Cookie = userSettingService.findByUserId(userId).getUserSetting().getCloud115Cookie();
        if (cloud115Cookie == null || cloud115Cookie.isEmpty()) {
            throw new BizException("115cloud_cookie_not_exist");
        }

        FormBody formBody = new FormBody.Builder()
                .add("cid", cid)
                .add("share_code", shareCode)
                .add("receive_code", receiveCode != null ? receiveCode : "")
                .add("file_id", fileId)
                .build();

        Request request = new Request.Builder()
                .url(WEB_API_BASE_URL + "/share/receive")
                .post(formBody)
                .addHeader("Cookie", cloud115Cookie)
                .build();

        try (Response response = webApiClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new BizException("api_not_complete","");
            }

            assert response.body() != null;
            String responseBody = response.body().string();
            JSONObject json = JSON.parseObject(responseBody);
            // 取出 data 对象
            if (!json.getBoolean("state")) {
                throw new BizException("115file_save_error",json.getString("error"));
            }
            JSONObject dataObj = json.getJSONObject("data");
            // 取出 data 对象
            return dataObj.getJSONObject("data");


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addOfflineDownload(String url, String savePath, String sign, String time, String uid, String userId) throws BizException {
        // 定义正则表达式
        Pattern magnetPattern = Pattern.compile("^magnet:\\?xt=urn:btih:", Pattern.CASE_INSENSITIVE);
        Pattern ed2kPattern = Pattern.compile("^ed2k://", Pattern.CASE_INSENSITIVE);

        boolean isMagnet = magnetPattern.matcher(url).find();
        boolean isEd2k = ed2kPattern.matcher(url).find();

        if (!isMagnet && !isEd2k) {
            throw new BizException("ed2k_pattern_no_pattern");
        }


        String cloud115Cookie = userSettingService.findByUserId(userId).getUserSetting().getCloud115Cookie();
        if (cloud115Cookie == null || cloud115Cookie.isEmpty()) {
            throw new BizException("115cloud_cookie_not_exist");
        }

        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("wp_path_id", savePath != null ? savePath : "0")
                .add("url", url);
        if (sign != null) formBuilder.add("sign", sign);
        if (time != null) formBuilder.add("time", time);
        if (uid != null) formBuilder.add("uid", uid);
        FormBody formBody = formBuilder.build();

        Request request = new Request.Builder()
                .url(LIXIAN_BASE_URL + "/web/lixian/?ct=lixian&ac=add_task_url")
                .post(formBody)
                .addHeader("Cookie", cloud115Cookie)
                .build();

        try (Response response = lixianClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new BizException("api_not_complete","");
            }

            assert response.body() != null;
            String responseBody = response.body().string();
            JSONObject json = JSON.parseObject(responseBody);

            if (!json.getBoolean("state")) {
                String errorMsg = json.getString("error_msg") != null ? json.getString("error_msg") : json.getString("error");
                throw new BizException("offline_download_error", errorMsg);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public cloud115OfflineDownload getOfflineDownloadParams(String userId) throws BizException {
        String cloud115Cookie = userSettingService.findByUserId(userId).getUserSetting().getCloud115Cookie();
        if (cloud115Cookie == null || cloud115Cookie.isEmpty()) {
            throw new BizException("115cloud_cookie_not_exist");
        }

        Request request = new Request.Builder()
                .url(LIXIAN_BASE_URL + "/?ct=offline&ac=space")
                .addHeader("Cookie", cloud115Cookie)
                .build();

        try (Response response = lixianClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new BizException("api_not_complete","");
            }

            // 由于 API 可能返回非预期数据，暂时使用硬编码值
            cloud115OfflineDownload params = new cloud115OfflineDownload();
            params.setSign(sign);
            params.setTime(String.valueOf(System.currentTimeMillis() / 1000));
            params.setUid(uid);
            return params;
        } catch (Exception e) {
            throw new BizException("offline_params_error",e.getMessage());
        }
    }
}
