package com.ducksoup.dilivideomain.mainservices;


import cn.dev33.satoken.stp.StpUtil;
import com.ducksoup.dilivideomain.utils.RedisUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeActionHandler {



    //被点赞的对象的id
    private String targetId;

    private RedisUtil redisUtil;

    //KEY的前缀，<PREFIX+targetId,Set<userId>>
    private String PREFIX;

    //更新频率锁 每三十分钟才能更新
    private String REDISLOCK;


    /**
     * （取消）点赞操作
     * @param consumer(String targetId,Long count) 更新点赞数量的操作
     * @return true为点赞 false为取消点赞
     */

    public boolean doHandel(BiConsumer<String,Long> consumer){

        Assert.notNull(targetId,"被（取消）点赞对象id不可以为null");

        String loginId = StpUtil.getLoginId("default");

        String key = PREFIX+targetId;

        boolean existSetItem = redisUtil.checkExistSetItem(key, loginId);

        boolean res = true;

        redisUtil.addToSet(key,loginId);

        if (existSetItem){
            redisUtil.rmFromSet(key,loginId);
            res = false;
        }


        if (redisUtil.get(REDISLOCK+targetId)==null){
            consumer.accept(targetId,redisUtil.countSetItem(key));
            redisUtil.set(REDISLOCK+targetId,1,3L, TimeUnit.MINUTES);
        }

        return res;

    }

}
