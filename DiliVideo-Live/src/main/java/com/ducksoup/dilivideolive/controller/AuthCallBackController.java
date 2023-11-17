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

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthCallBackController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private LiveRoomService liveRoomService;

    @Autowired
    private HttpServletRequest request;


    @Autowired
    private LiveInfoService liveInfoService;

    /**
     * 开启直播时的回调 Nginx会携带原来的参数回调此接口
     *
     * @param params OnPublishParams
     * @return 200状态码允许推流 其他状态码拒绝推流
     * @see OnPublishParams 包含Key和
     */
    @PostMapping("/on_publish")
    ResponseEntity<Void> onPublish(OnPublishParams params) {



        //解密key获取RoomId 判断合法性
        Map<String,String> roomInfo = liveRoomService.getRoomInfoByKey(params.getKey());

        String roomId = roomInfo.get("roomId");
        if (roomId == null || !roomId.equals(params.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }


        LiveRoom liveRoom = liveRoomService.getById(roomId);

        try {
            liveInfoService.initRoomInfo(liveRoom);
            liveInfoService.initRoomControlInfo(params,roomInfo);
        } catch (Exception e) {
            liveInfoService.deleteRoomInfo(liveRoom);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }


        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @PostMapping("/on_publish_done")
    ResponseEntity<Void> onPublishDone(OnPublishParams params) {

        Map<String,String> roomInfo = liveRoomService.getRoomInfoByKey(params.getKey());
        if (roomInfo == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String roomId = roomInfo.get("roomId");

        LiveRoom liveRoom = liveRoomService.getById(roomId);

        //设置直播状态为0

        redisUtil.remove(CONSTANT_LIVE.LIVE_SECRET_KEY + params.getKey());

        //删除房间直播信息
        liveInfoService.deleteRoomInfo(liveRoom);

        return ResponseEntity.status(HttpStatus.OK).build();

    }


}
