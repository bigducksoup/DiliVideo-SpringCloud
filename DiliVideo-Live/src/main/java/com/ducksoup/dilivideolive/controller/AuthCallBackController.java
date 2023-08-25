package com.ducksoup.dilivideolive.controller;


import com.ducksoup.dilivideoentity.constant.CONSTANT_LIVE;
import com.ducksoup.dilivideolive.Entity.LiveRoom;
import com.ducksoup.dilivideolive.controller.params.OnPublishParams;
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

    /**
     * 开启直播时的回调 Nginx会携带原来的参数回调此接口
     * @param params OnPublishParams 包含Key和random
     * @return 200状态码允许推流 其他状态码拒绝推流
     */
    @PostMapping("/on_publish")
    ResponseEntity<Void> onPublish(@RequestBody OnPublishParams params) {
        // TODO 权限校验


        //解密key获取RoomId 判断合法性
        String roomId = liveRoomService.getRoomIdBykey(params.getKey());
        if (roomId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        //判断随机uuid是否被篡改，此UUID是推流后为了防止 hls指定文件夹重复
        String uuid = (String) redisUtil.get(CONSTANT_LIVE.LIVE_RANDOM_URL_SUFFIX_KEY + roomId);
        if (!uuid.equals(params.getRandom())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        LiveRoom liveRoom = liveRoomService.getById(roomId);

        //设置直播状态为1
        liveRoom.setIsLive(1);
        liveRoomService.updateById(liveRoom);

        //获取推流服务器的ipAndPort
        String ipAndPort = (String) redisUtil.get(CONSTANT_LIVE.LIVE_IP_PORT_KEY+roomId);

        //http://123.456.31.31:1935/live/roomId::uuid_SD
        //360p
        String SD = "http://"+ipAndPort+"/live/"+roomId+"::"+uuid+"_SD";
        //720p
        String HD =  "http://"+ipAndPort+"/live/"+roomId+"::"+uuid+"_HD";
        //1080p
        String FHD = "http://"+ipAndPort+"/live/"+roomId+"::"+uuid+"_FHD";

        redisUtil.mapPut(CONSTANT_LIVE.LIVE_PLAY_URL+roomId,"SD",SD);
        redisUtil.mapPut(CONSTANT_LIVE.LIVE_PLAY_URL+roomId,"HD",HD);
        redisUtil.mapPut(CONSTANT_LIVE.LIVE_PLAY_URL+roomId,"FHD",FHD);

        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @PostMapping("/on_publish_done")
    ResponseEntity<Void> onPublishDone(@RequestBody OnPublishParams params) {

        String roomId = liveRoomService.getRoomIdBykey(params.getKey());
        if (roomId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        LiveRoom liveRoom = liveRoomService.getById(roomId);

        //设置直播状态为0
        liveRoom.setIsLive(0);
        liveRoomService.updateById(liveRoom);


        //删除关于直播room的所有信息
        redisUtil.remove(CONSTANT_LIVE.LIVE_SECRET_KEY+params.getKey());
        redisUtil.remove(CONSTANT_LIVE.LIVE_IP_PORT_KEY+roomId);
        redisUtil.remove(CONSTANT_LIVE.LIVE_PLAY_URL+roomId);
        redisUtil.remove(CONSTANT_LIVE.LIVE_RANDOM_URL_SUFFIX_KEY + roomId);


        return ResponseEntity.status(HttpStatus.OK).build();

    }


}
