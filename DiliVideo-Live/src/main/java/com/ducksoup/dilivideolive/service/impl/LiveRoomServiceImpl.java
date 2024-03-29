package com.ducksoup.dilivideolive.service.impl;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ducksoup.dilivideoentity.constant.CONSTANT_LIVE;
import com.ducksoup.dilivideolive.entity.LiveRoom;
import com.ducksoup.dilivideolive.service.LiveRoomService;
import com.ducksoup.dilivideolive.mapper.LiveRoomMapper;
import com.ducksoup.dilivideolive.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
* @author meichuankutou
* @description 针对表【live_room】的数据库操作Service实现
* @createDate 2023-08-09 10:43:29
*/
@Service
public class LiveRoomServiceImpl extends ServiceImpl<LiveRoomMapper, LiveRoom>
    implements LiveRoomService{

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public LiveRoom checkRoomReady(String loginId) {

        LiveRoom liveRoom = getOne(new LambdaQueryWrapper<LiveRoom>().eq(LiveRoom::getUserId, loginId));
        if (liveRoom==null)return null;

        if (liveRoom.getIsLive() == 1)return null;

        if (liveRoom.getStatus() == 0)return null;

        return liveRoom;

    }

    /**
     * 获取信息
     * @param key 加密后的内容
     * @return Map<String,String>
     *         key:roomId value:roomId
     *         key:userId value:userId
     *         key:appName value:appName
     *         key:serverAddr value:serverAddr
     */
    @Override
    public Map<String,String> getRoomInfoByKey(String key) {

        //从redis获取key
        byte[] secretKey = (byte[]) redisUtil.get(CONSTANT_LIVE.LIVE_SECRET_KEY+key);
        if (secretKey==null)return null;

        AES aes = SecureUtil.aes(secretKey);

        String content = aes.decryptStr(key);

        Map<String,String> map = JSON.parseObject(content, HashMap.class);


        return  map;
    }




}




