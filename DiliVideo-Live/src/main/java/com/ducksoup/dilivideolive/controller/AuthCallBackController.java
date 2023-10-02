package com.ducksoup.dilivideolive.controller;


import com.ducksoup.dilivideoentity.constant.CONSTANT_LIVE;
import com.ducksoup.dilivideolive.entity.LiveRoom;
import com.ducksoup.dilivideolive.controller.params.OnPublishParams;
import com.ducksoup.dilivideolive.mainservices.LiveInfoService;
import com.ducksoup.dilivideolive.service.LiveRoomService;
import com.ducksoup.dilivideolive.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthCallBackController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private LiveRoomService liveRoomService;


    @Autowired
    private LiveInfoService liveInfoService;

    /**
     * 开启直播时的回调 Nginx会携带原来的参数回调此接口
     * @param params  OnPublishParams
     * @see OnPublishParams 包含Key和
     * @return 200状态码允许推流 其他状态码拒绝推流
     */
    @PostMapping("/on_publish")
    ResponseEntity<Void> onPublish(OnPublishParams params) {

        //解密key获取RoomId 判断合法性
        String roomId = liveRoomService.getRoomIdBykey(params.getKey());
        if (roomId == null || !roomId.equals(params.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }



        LiveRoom liveRoom = liveRoomService.getById(roomId);

        new Thread(()->{
            try {
                liveInfoService.initRoomInfo(liveRoom);
            }catch (Exception e){
                //TODO kick Publish Client
                liveInfoService.deleteRoomInfo(liveRoom);
            }
        }).start();



        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @PostMapping("/on_publish_done")
    ResponseEntity<Void> onPublishDone(OnPublishParams params) {

        String roomId = liveRoomService.getRoomIdBykey(params.getKey());
        if (roomId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        LiveRoom liveRoom = liveRoomService.getById(roomId);

        //设置直播状态为0

        redisUtil.remove(CONSTANT_LIVE.LIVE_SECRET_KEY+params.getKey());

        //删除房间直播信息
        liveInfoService.deleteRoomInfo(liveRoom);

        return ResponseEntity.status(HttpStatus.OK).build();

    }








}
