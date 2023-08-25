package com.ducksoup.dilivideolive.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.http.HttpStatus;

import com.ducksoup.dilivideoentity.result.ResponseResult;
import com.ducksoup.dilivideolive.Entity.LiveRoom;
import com.ducksoup.dilivideolive.service.LiveRoomService;
import com.ducksoup.dilivideolive.service.LiveServerService;
import com.ducksoup.dilivideolive.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/publish")
public class PublishController {


    @Autowired
    private LiveRoomService liveRoomService;

    @Autowired
    private LiveServerService liveServerService;

    @Autowired
    private RedisUtil redisUtil;


    @SaCheckRole("ANCHOR")
    @SaCheckLogin
    @GetMapping("/get_rtmp_publish_url")
    public ResponseResult<String> getPublishUrl() {

        // rtmp://127.0.0.1:1938/live/roomId?userId=#{userId}&key=#{key}
        String loginId = StpUtil.getLoginId("empty");


        LiveRoom liveRoom = liveRoomService.checkRoomReady(loginId);
        if (liveRoom == null) {
            return new ResponseResult<>(HttpStatus.HTTP_FORBIDDEN, "无法开启直播");
        }


        //获取rtmp推流链接
        String rtmpUrl = liveServerService.loadBalanceChooseServer(liveRoom, loginId);
        if (rtmpUrl == null) {
            return new ResponseResult<>(HttpStatus.HTTP_NOT_ACCEPTABLE, "请稍后再试");
        }


        return new ResponseResult<>(HttpStatus.HTTP_OK, "获取成功", rtmpUrl);
    }


}
