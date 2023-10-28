package com.ducksoup.dilivideocontent.mainservices.UserOperation.Impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ducksoup.dilivideocontent.entity.Videoinfo;
import com.ducksoup.dilivideocontent.mainservices.UserOperation.LikeOperationService;
import com.ducksoup.dilivideocontent.mainservices.UserOperation.RefreshDataService;
import com.ducksoup.dilivideocontent.service.VideoinfoService;
import com.ducksoup.dilivideocontent.utils.RedisUtil;
import com.ducksoup.dilivideoentity.vo.VideoInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class LikeOperationServiceImpl implements LikeOperationService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private VideoinfoService videoinfoService;

    @Autowired
    private RefreshDataService refreshDataService;


    private static final String VIDEO_LIKE_COUNT = "videoLikeCount:";

    private static final String USER_LIKE_VIDEO = "userLikeVideo:";



    @Override
    public Long likeVideo(String userId, String videoInfoId) {

        redisUtil.addToSet(USER_LIKE_VIDEO+userId,videoInfoId);
        if (redisUtil.get(VIDEO_LIKE_COUNT+videoInfoId) == null){
            redisUtil.set(VIDEO_LIKE_COUNT+videoInfoId,0);
        }

        redisUtil.increaseKey(VIDEO_LIKE_COUNT+videoInfoId);

        return this.updateLikeCount(videoInfoId);

    }

    @Override
    public Long unlikeVideo(String userId,String videoInfoId){
        redisUtil.rmFromSet(USER_LIKE_VIDEO+userId,videoInfoId);
        redisUtil.decreaseKey(VIDEO_LIKE_COUNT+videoInfoId);

        return this.updateLikeCount(videoInfoId);

    }

    @Override
    public boolean checkLikeVideo(String userId, String videoInfoId) {

        updateLikeCount(videoInfoId);

        return redisUtil.checkExistSetItem(USER_LIKE_VIDEO+userId,videoInfoId);
    }

    @Override
    public void setVideoLikeStatus(List<VideoInfoVo> videoInfoVos) {
        if (!StpUtil.isLogin()){
            videoInfoVos.forEach(item->{
                item.setLiked(false);
            });
        }else {
            videoInfoVos.forEach(item->{
                item.setLiked(checkLikeVideo(StpUtil.getLoginId().toString(),item.getVideoInfoId()));
                if (item.isLiked()){
                    item.setLikeCount(item.getLikeCount()+1);
                }
            });
        }

    };

    @Override
    public Long updateLikeCount(String videoInfoId) {
        String refreshkey = "refreshVideoLike:" + videoInfoId;




        long likeCount = (long) redisUtil.get(VIDEO_LIKE_COUNT+videoInfoId);


        refreshDataService.refreshData(likeCount,refreshkey,(count) -> {
            videoinfoService.update(
                    new LambdaUpdateWrapper<Videoinfo>()
                            .eq(Videoinfo::getId, videoInfoId)
                            .set(Videoinfo::getLikeCount, count));
        });

        return likeCount;
    }



}
