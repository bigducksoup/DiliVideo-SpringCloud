package com.ducksoup.dilivideocontent.mainservices.UserOperation.Impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ducksoup.dilivideocontent.Entity.Videoinfo;
import com.ducksoup.dilivideocontent.mainservices.UserOperation.LikeOperationService;
import com.ducksoup.dilivideocontent.service.VideoinfoService;
import com.ducksoup.dilivideocontent.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class LikeOperationServiceImpl implements LikeOperationService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private VideoinfoService videoinfoService;

    @Override
    public void likeVideo(String userId, String videoInfoId) {

        redisUtil.addToSet("videoLike:"+videoInfoId,userId);

        this.updateLikeCount(videoInfoId);

    }

    @Override
    public void unlikeVideo(String userId,String videoInfoId){
        redisUtil.rmFromSet("videoLike:"+videoInfoId,userId);

        this.updateLikeCount(videoInfoId);

    }

    @Override
    public boolean checkLikeVideo(String userId, String videoInfoId) {

        updateLikeCount(videoInfoId);

        return redisUtil.checkExistSetItem("videoLike:"+videoInfoId,userId);
    }

    @Override
    public boolean updateLikeCount(String videoInfoId) {
        String refreshkey = "refreshVideoLike:";

        if (redisUtil.exists(refreshkey+videoInfoId)){
            return true;
        }


        redisUtil.set(refreshkey+videoInfoId,null,5L, TimeUnit.MINUTES);

        long likeCount = redisUtil.countSetItem("videoLike:"+videoInfoId);

        boolean update = videoinfoService.update(
                new LambdaUpdateWrapper<Videoinfo>()
                        .eq(Videoinfo::getId, videoInfoId)
                        .set(Videoinfo::getLikeCount, likeCount)
        );

        if (update){
            log.info("更新"+videoInfoId +"like数量为" + likeCount);
        }

        return update;
    }



}
