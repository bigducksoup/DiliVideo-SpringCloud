package com.ducksoup.dilivideolive.mapper;

import com.ducksoup.dilivideolive.Entity.LiveServer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author meichuankutou
* @description 针对表【live_server】的数据库操作Mapper
* @createDate 2023-08-08 20:59:32
* @Entity com.ducksoup.dilivideolive.Entity.LiveServer
*/
public interface LiveServerMapper extends BaseMapper<LiveServer> {


    LiveServer getByConnectionCount();

}




