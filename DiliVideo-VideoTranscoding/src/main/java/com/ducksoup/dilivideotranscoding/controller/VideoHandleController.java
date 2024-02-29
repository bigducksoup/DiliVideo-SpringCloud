package com.ducksoup.dilivideotranscoding.controller;


import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSONUtil;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import com.ducksoup.dilivideotranscoding.controller.param.MinIORelated.MinIOSourceFileCombineParam;
import com.ducksoup.dilivideotranscoding.controller.param.MinIORelated.MinIOSourceTranscodeParam;
import com.ducksoup.dilivideotranscoding.entity.CallBack;
import com.ducksoup.dilivideotranscoding.service.MyFileCombineService;
import com.ducksoup.dilivideotranscoding.service.compisition.VideoService;
import com.ducksoup.dilivideotranscoding.service.compisition.result.MinIOSourceCombineResult;
import com.ducksoup.dilivideotranscoding.service.compisition.result.MinIOSourceTranscodeResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/video/handle")
@AllArgsConstructor
@Slf4j
public class VideoHandleController {


    private VideoService videoService;


    @PostMapping("/minio-source-transcode")
    public ResponseResult<Boolean> MinIOSourceTranscode(@Valid @RequestBody MinIOSourceTranscodeParam param) {

        ThreadUtil.execAsync(() -> {
            // 执行业务
            log.info("开始执行视频转码任务 param:{}", JSONUtil.toJsonStr(param));
            MinIOSourceTranscodeResult result = videoService.MinIOSourceTranscode(param);

            // 获取回调信息
            CallBack callBack = param.getCallBack();

            if (callBack == null) {
                return;
            }

            // 回调
            HttpUtil.createRequest(Method.valueOf(callBack.getMethod()), callBack.getUrl())
                    .body(JSONUtil.toJsonStr(result))
                    .form(callBack.getForm())
                    .addHeaders(callBack.getHeaders())
                    .execute();
        });

        return ResponseResult.success();
    }


    @PostMapping("minio-source-combine")
    public ResponseResult<MinIOSourceCombineResult> MinIOSourceCombine(@Valid @RequestBody MinIOSourceFileCombineParam param) {
        MinIOSourceCombineResult result = videoService.MinIOSourceCombine(param);
        return ResponseResult.success(result);
    }

}
