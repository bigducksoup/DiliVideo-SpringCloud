package com.ducksoup.dilivideocontent.mainservices.UserOperation.Impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ducksoup.dilivideocontent.entity.Videoinfo;
import com.ducksoup.dilivideocontent.mainservices.UserOperation.LikeOperationService;
import com.ducksoup.dilivideocontent.mainservices.UserOperation.RefreshDataService;
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

    @Autowired
    private RefreshDataService refreshDataService;

    @Override
    public Long likeVideo(String userId, String videoInfoId) {

        redisUtil.addToSet("videoLike:"+videoInfoId,userId);

        return this.updateLikeCount(videoInfoId);

    }

    @Override
    public Long unlikeVideo(String userId,String videoInfoId){
        redisUtil.rmFromSet("videoLike:"+videoInfoId,userId);

        return this.updateLikeCount(videoInfoId);

    }

    @Override
    public boolean checkLikeVideo(String userId, String videoInfoId) {

        updateLikeCount(videoInfoId);

        return redisUtil.checkExistSetItem("videoLike:"+videoInfoId,userId);
    }

    @Override
    public Long updateLikeCount(String videoInfoId) {
        String refreshkey = "refreshVideoLike:";

        long likeCount = redisUtil.countSetItem("videoLike:"+videoInfoId);


        refreshDataService.refreshData(likeCount,refreshkey,(count) -> {
            videoinfoService.update(
                    new LambdaUpdateWrapper<Videoinfo>()
                            .eq(Videoinfo::getId, videoInfoId)
                            .set(Videoinfo::getLikeCount, count));
        });

        return likeCount;
    }



}
