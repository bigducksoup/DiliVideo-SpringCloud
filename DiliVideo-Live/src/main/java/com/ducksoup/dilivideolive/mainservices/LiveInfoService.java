package com.ducksoup.dilivideolive.mainservices;

import com.ducksoup.dilivideolive.entity.LivePlayServer;
import com.ducksoup.dilivideolive.entity.LivePlayUrls;
import com.ducksoup.dilivideolive.entity.LiveRoom;
import com.ducksoup.dilivideolive.entity.LiveServer;

import java.util.List;

public interface LiveInfoService {

    void initRoomInfo(LiveRoom liveRoom);


    List<LivePlayUrls> getPlayUrl(List<LivePlayServer> playServers,String roomId);

    List<LivePlayServer> getPlayServerApplication(LiveServer liveServer);


}
