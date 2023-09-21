package com.ducksoup.dilivideolive.service;

import com.ducksoup.dilivideolive.entity.LiveRoom;
import com.ducksoup.dilivideolive.entity.LiveServer;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author meichuankutou
* @description 针对表【live_server】的数据库操作Service
* @createDate 2023-08-08 20:59:32
*/
public interface LiveServerService extends IService<LiveServer> {

    String loadBalanceChooseServer(LiveRoom room,String userId);

}
