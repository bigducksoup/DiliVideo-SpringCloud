package com.ducksoup.dilivideolive.service;

import com.ducksoup.dilivideolive.entity.LiveRoom;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
* @author meichuankutou
* @description 针对表【live_room】的数据库操作Service
* @createDate 2023-08-09 10:43:29
*/
public interface LiveRoomService extends IService<LiveRoom> {

    LiveRoom checkRoomReady(String loginId);


    Map<String,String> getRoomInfoByKey(String key);

}
