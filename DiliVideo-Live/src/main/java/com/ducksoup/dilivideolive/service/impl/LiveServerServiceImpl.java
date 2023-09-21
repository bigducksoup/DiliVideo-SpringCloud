package com.ducksoup.dilivideolive.service.impl;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ducksoup.dilivideoentity.constant.CONSTANT_LIVE;
import com.ducksoup.dilivideolive.entity.LiveRoom;
import com.ducksoup.dilivideolive.entity.LiveServer;
import com.ducksoup.dilivideolive.service.LiveServerService;
import com.ducksoup.dilivideolive.mapper.LiveServerMapper;
import com.ducksoup.dilivideolive.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
* @author meichuankutou
* @description 针对表【live_server】的数据库操作Service实现
* @createDate 2023-08-08 20:59:32
*/
@Service
public class LiveServerServiceImpl extends ServiceImpl<LiveServerMapper, LiveServer>
    implements LiveServerService{

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public String loadBalanceChooseServer(LiveRoom room, String userId) {

        // TODO 根据配置权重选择
        LiveServer server = this.baseMapper.getByConnectionCount();

        if (server.getConnectionCount() > 30)return null;

        String ipAndPort = server.getDomain() + ":" + server.getPort();

        //baseUrl
        String baseUrl = server.getProtocol()+"://"+ipAndPort;

        //加密前的内容
        String content = room.getId()+"::::::::"+userId;

        SecretKey secretKey = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue());

        AES aes = SecureUtil.aes(secretKey.getEncoded());

        //加密后的内容
        String encryptHex = aes.encryptHex(content);


        redisUtil.set(CONSTANT_LIVE.LIVE_SECRET_KEY+encryptHex,secretKey.getEncoded(),10L, TimeUnit.MINUTES);
        redisUtil.set(CONSTANT_LIVE.LIVE_PUSH_SERVER_KEY+room.getId(),server,10L, TimeUnit.MINUTES);



        //生成pushUrl
        //向此url推流会通过exec命令调用ffmpeg重新推到liveUrl
        String pushUrl = baseUrl+"/push"+"/"+room.getId() + "?key="+encryptHex;

        return pushUrl;
    }
}




