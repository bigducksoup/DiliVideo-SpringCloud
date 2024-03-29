package com.ducksoup.dilivideolive.mainservices;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.ducksoup.dilivideoentity.constant.CONSTANT_LIVE;
import com.ducksoup.dilivideolive.controller.params.OnPublishParams;
import com.ducksoup.dilivideolive.entity.*;
import com.ducksoup.dilivideolive.service.LivePlayServerService;
import com.ducksoup.dilivideolive.service.LiveRoomService;
import com.ducksoup.dilivideolive.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class LiveInfoService {

    @Autowired
    private LiveRoomService liveRoomService;

    @Autowired
    private RedisUtil redisUtil;


    @Autowired
    private LivePlayServerService livePlayServerService;

    @Value("${live.maxConnPerApp}")
    private Integer maxPlayCount;


    public void initRoomInfo(LiveRoom liveRoom) {

        //设置直播状态为1
        liveRoom.setIsLive(1);
        LiveInfo liveInfo = new LiveInfo();

        liveInfo.setRoomId(liveRoom.getId());
        liveInfo.setUserId(liveRoom.getUserId());
        liveInfo.setStartTime(DateTime.now());

        //获取LiveServer
        LiveServer liveServer = (LiveServer) redisUtil.get(CONSTANT_LIVE.LIVE_PUSH_SERVER_KEY + liveRoom.getId());

        List<LivePlayServer> playServers = getPlayServerApplication(liveServer);

        List<LivePlayUrls> playUrlsSet = getPlayUrl(playServers, liveInfo.getRoomId());

        liveInfo.setLivePlayUrlSet(playUrlsSet);

        //设置直播信息
        redisUtil.set(CONSTANT_LIVE.LIVE_ROOM_INFO + liveRoom.getId(), liveInfo);


        liveRoomService.updateById(liveRoom);


    }




    public void initRoomControlInfo(OnPublishParams params,Map<String,String> roomInfo) {

        LiveControlInfo liveControlInfo = new LiveControlInfo();
        liveControlInfo.setId(UUID.randomUUID().toString());
        liveControlInfo.setLiveRoomId(roomInfo.get("roomId"));
        liveControlInfo.setServerUrl(roomInfo.get("serverAddr"));
        liveControlInfo.setAddr(params.getAddr());
        liveControlInfo.setCallConnect(params.getCall());
        liveControlInfo.setClientId(params.getClientid());
        liveControlInfo.setApp(params.getApp());
        liveControlInfo.setFlashVer(params.getFlashVer());
        liveControlInfo.setSwfUrl(params.getSwfUrl());
        liveControlInfo.setTcUrl(params.getTcUrl());
        liveControlInfo.setPageUrl(params.getPageUrl());
        liveControlInfo.setName(params.getName());

        Db.save(liveControlInfo);
    }



    public void deleteRoomInfo(LiveRoom liveRoom) {
        liveRoom.setIsLive(0);
        liveRoomService.updateById(liveRoom);

        Db.remove(Wrappers.lambdaQuery(LiveControlInfo.class).eq(LiveControlInfo::getLiveRoomId,liveRoom.getId()));
        //删除关于直播room的所有信息

        redisUtil.remove(CONSTANT_LIVE.LIVE_PUSH_SERVER_KEY+liveRoom.getId());
        redisUtil.remove(CONSTANT_LIVE.LIVE_PLAY_URL+liveRoom.getId());
        redisUtil.remove(CONSTANT_LIVE.LIVE_RANDOM_URL_SUFFIX_KEY + liveRoom.getId());
        redisUtil.remove(CONSTANT_LIVE.LIVE_ROOM_INFO + liveRoom.getId());

    }


    /**
     * 获取直播播放链接，
     *
     * @param playServers List<LivePlayServer>
     * @param roomId      直播间Id
     * @return List<LivePlayUrls>
     *
     * <p>[{type:"HLS";HD:[],SD:[],FHD:[]}]</p>
     */

    public List<LivePlayUrls> getPlayUrl(List<LivePlayServer> playServers, String roomId) {


        Map<String, List<LivePlayServer>> collect = playServers.stream()
                .filter(item -> item.getConnectionCount() < maxPlayCount)
                .collect(Collectors.groupingBy(item -> {
                    if (item.getQuality().equals(1)) {
                        return "SD";
                    } else if (item.getQuality().equals(2)) {
                        return "HD";
                    } else if (item.getQuality().equals(3)) {
                        return "FHD";
                    }
                    return "invalid";
                }));


        LivePlayUrls HLSPlayUrls = new LivePlayUrls();
        LivePlayUrls RTMPPLayUrls = new LivePlayUrls();

        collect.getOrDefault("SD",new ArrayList<>()).forEach(item -> {
            String url = buildPlayUrl(item, roomId);

            boolean b = item.getHls().equals(1) ? HLSPlayUrls.getSD().add(url) : RTMPPLayUrls.getSD().add(url);
        });

        collect.getOrDefault("HD",new ArrayList<>()).forEach(item -> {
            String url = buildPlayUrl(item, roomId);

            boolean b = item.getHls().equals(1) ? HLSPlayUrls.getHD().add(url) : RTMPPLayUrls.getHD().add(url);

        });

        collect.getOrDefault("FHD",new ArrayList<>()).forEach(item -> {
            String url = buildPlayUrl(item, roomId);
            boolean b = item.getHls().equals(1) ? HLSPlayUrls.getFHD().add(url) : RTMPPLayUrls.getFHD().add(url);
        });

        List<LivePlayUrls> res = new ArrayList<>();
        res.add(HLSPlayUrls);
        res.add(RTMPPLayUrls);

        return res;
    }

    /**
     * 通过application生成播放链接
     *
     * @param application LivePlayServer 播放服务器
     * @param roomId      roomId 直播间Id
     * @return URL;
     */
    public String buildPlayUrl(LivePlayServer application, String roomId) {

        String domain = application.getIp();

        if (application.getDomain() != null) {
            domain = application.getDomain();
        }

        String port = application.getPort();

        String httpPort = application.getHttpPort();

        String path = application.getHttpPath();

        String protocol = application.getProtocol();

        String applicationName = application.getApplicationName();

        String afterProtocol = "://";

        //判断HLS
        if (application.getHls().equals(1)) {
            return application.getHttpProtocol() + afterProtocol + domain + httpPort + path + roomId;
        }
        //否则使用RTMP
        //rtmp://null1935/watch360p/1
        return protocol + afterProtocol + domain +":"+ port + "/" + applicationName + "/" + roomId;
    }


    /**
     * 根据推流服务器，找到所有能使用的播放服务器
     *
     * @param liveServer LiveServer
     * @return List<LivePlayServer>
     * @see LiveServer
     */

    public List<LivePlayServer> getPlayServerApplication(LiveServer liveServer) {
        List<String> pushServerIds = new ArrayList<>();
        List<LivePlayServer> playServers = new ArrayList<>();
        pushServerIds.add(liveServer.getId());

        //循环找出所有参与传播的application
        while (!pushServerIds.isEmpty()) {
            List<LivePlayServer> list = livePlayServerService.list(new LambdaQueryWrapper<LivePlayServer>().in(LivePlayServer::getPushServerId, pushServerIds).eq(LivePlayServer::getStatus, 1));
            playServers.addAll(list);
            pushServerIds.clear();
            pushServerIds.addAll(list.stream().map(LivePlayServer::getId).collect(Collectors.toList()));
        }

        return playServers;
    }




}
