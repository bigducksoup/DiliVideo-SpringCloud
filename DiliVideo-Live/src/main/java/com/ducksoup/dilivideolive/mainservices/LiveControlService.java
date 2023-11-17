package com.ducksoup.dilivideolive.mainservices;


import cn.hutool.core.net.url.UrlBuilder;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.ducksoup.dilivideolive.entity.LiveControlInfo;
import com.ducksoup.dilivideolive.enums.LiveControlAction;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class LiveControlService {


    private final String controlPath = "control";

    public boolean dropClient(String roomId) {

        //获取控制信息
        LiveControlInfo info = Db.lambdaQuery(LiveControlInfo.class).eq(LiveControlInfo::getLiveRoomId, roomId).one();


        String protocol = "http";

        String baseUrl = protocol + "://" + info.getServerUrl();

        Map<String, String> params = new HashMap<>();

        if (info.getAddr() != null) {
            params.put("addr", info.getAddr());
        }

        if (info.getApp() != null) {
            params.put("app", info.getApp());
        }

        if (info.getName() != null) {
            params.put("name", info.getName());
        }

        if (info.getClientId() != null) {
            params.put("clientid", info.getClientId());
        }


        String controlUrl = buildControlUrl(baseUrl, controlPath, LiveControlAction.DROP, params, "client");


        return doRequest(controlUrl);

    }

    public boolean startRecord(String roomId) {
        //获取控制信息
        LiveControlInfo info = Db.lambdaQuery(LiveControlInfo.class).eq(LiveControlInfo::getLiveRoomId, roomId).one();


        String protocol = "http";

        String baseUrl = protocol + "://" + info.getServerUrl();

        Map<String, String> params = new HashMap<>();


        if (info.getApp() != null) {
            params.put("app", info.getApp());
        }

        if (info.getName() != null) {
            params.put("name", info.getName());
        }

        String controlUrl = buildControlUrl(baseUrl, controlPath, LiveControlAction.RECORD, params, "start");

        return doRequest(controlUrl);

    }

    public boolean stopRecord(String roomId) {
        //获取控制信息
        LiveControlInfo info = Db.lambdaQuery(LiveControlInfo.class).eq(LiveControlInfo::getLiveRoomId, roomId).one();


        String protocol = "http";

        String baseUrl = protocol + "://" + info.getServerUrl();

        Map<String, String> params = new HashMap<>();


        if (info.getApp() != null) {
            params.put("app", info.getApp());
        }

        if (info.getName() != null) {
            params.put("name", info.getName());
        }

        String controlUrl = buildControlUrl(baseUrl, controlPath, LiveControlAction.RECORD, params, "stop");

        return doRequest(controlUrl);
    }

    public boolean redirect(String roomId, String newName) {

        //获取控制信息
        LiveControlInfo info = Db.lambdaQuery(LiveControlInfo.class).eq(LiveControlInfo::getLiveRoomId, roomId).one();


        String protocol = "http";

        String baseUrl = protocol + "://" + info.getServerUrl();

        Map<String, String> params = new HashMap<>();

        params.put("newName", newName);

        if (info.getApp() != null) {
            params.put("app", info.getApp());
        }

        if (info.getName() != null) {
            params.put("name", info.getName());
        }

        if (info.getAddr() != null) {
            params.put("addr", info.getAddr());
        }

        if (info.getClientId() != null) {
            params.put("clientid", info.getClientId());
        }


        String controlUrl = buildControlUrl(baseUrl, controlPath, LiveControlAction.REDIRECT, params, "client");

        return doRequest(controlUrl);

    }
    private boolean doRequest(String controlUrl) {

        log.info("controlUrl:{}", controlUrl);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(controlUrl)
                .build();


        try {
            Response response = client.newCall(request).execute();

            if (response.code() != 200) {
                return false;
            }

        } catch (IOException e) {
            log.error(e.getMessage());
            return false;
        }

        return true;
    }

    public String buildControlUrl(String baseUrl, String controlPath, LiveControlAction action, Map<String, String> params, String... pathSegments) {

        Assert.notNull(baseUrl, "baseUrl 不能为空");

        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment(controlPath)
                .addPathSegment(action.value);

        for (String pathSegment : pathSegments) {
            builder.addPathSegment(pathSegment);
        }

        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.addQueryParameter(entry.getKey(), entry.getValue());
        }

        return builder.build().toString();
    }


}
