package com.ducksoup.dilivideolive.service;

import com.ducksoup.dilivideolive.Entity.LiveRoom;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author meichuankutou
* @description 针对表【live_room】的数据库操作Service
* @createDate 2023-08-09 10:43:29
*/
public interface LiveRoomService extends IService<LiveRoom> {

    LiveRoom checkRoomReady(String loginId);


    String getRoomIdBykey(String key);

}
