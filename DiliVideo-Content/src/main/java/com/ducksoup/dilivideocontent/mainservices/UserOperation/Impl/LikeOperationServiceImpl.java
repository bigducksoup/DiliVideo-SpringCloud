package com.ducksoup.dilivideocontent.mainservices.UserOperation.Impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ducksoup.dilivideocontent.aop.annonation.PerformanceLog;
import com.ducksoup.dilivideocontent.entity.Videoinfo;
import com.ducksoup.dilivideocontent.mainservices.UserOperation.LikeOperationService;
import com.ducksoup.dilivideocontent.mainservices.UserOperation.RefreshDataService;
import com.ducksoup.dilivideocontent.service.VideoinfoService;
import com.ducksoup.dilivideocontent.utils.RedisUtil;
import com.ducksoup.dilivideoentity.vo.VideoInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LikeOperationServiceImpl implements LikeOperationService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private VideoinfoService videoinfoService;

    @Autowired
    private RefreshDataService refreshDataService;

    @Autowired
    private ThreadPoolTaskExecutor IOThreadPool;


    private static final String VIDEO_LIKE_COUNT = "videoLikeCount:";

    private static final String USER_LIKE_VIDEO = "userLikeVideo:";



    @Override
    public Long likeVideo(String userId, String videoInfoId) {

        redisUtil.addToSet(USER_LIKE_VIDEO+userId,videoInfoId);
        if (redisUtil.get(VIDEO_LIKE_COUNT+videoInfoId) == null){
            redisUtil.set(VIDEO_LIKE_COUNT+videoInfoId,0);
        }

        if (redisUtil.exists(VIDEO_LIKE_COUNT+videoInfoId)){
            redisUtil.increaseKey(VIDEO_LIKE_COUNT+videoInfoId);
        }else {
            redisUtil.set(VIDEO_LIKE_COUNT+videoInfoId,1);
        }



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

        IOThreadPool.submit(()->{
            updateLikeCount(videoInfoId);
        });

        return redisUtil.checkExistSetItem(USER_LIKE_VIDEO+userId,videoInfoId);
    }

    @Override
    public void setVideoLikeStatus(List<VideoInfoVo> videoInfoVos) {

        boolean login = StpUtil.isLogin();

        if (!login){
            for (VideoInfoVo videoInfoVo : videoInfoVos) {
                videoInfoVo.setLiked(false);
            }
            return;
        }

        String loginId = (String) StpUtil.getLoginId();

        List<String> ids = videoInfoVos.stream().map(VideoInfoVo::getVideoInfoId).collect(Collectors.toList());


        //TODO BUG Unknown redis exception; nested exception is java.lang.IllegalArgumentException: Members must not be empty
        Map<String, Boolean> booleanMap = redisUtil.isSetMember(USER_LIKE_VIDEO + loginId, ids);


        videoInfoVos.forEach(item->{

            item.setLiked(booleanMap.get(item.getVideoInfoId()));
            if (item.isLiked()){
                item.setLikeCount(item.getLikeCount()+1);
            }

        });

        IOThreadPool.submit(()->{
            booleanMap.keySet().forEach(this::updateLikeCount);
        });

    }

    @Override
    public void setVideoLikeStatus(VideoInfoVo videoInfoVo) {

        if (!StpUtil.isLogin()){
            videoInfoVo.setLiked(false);
            return;
        }


        String loginId = (String) StpUtil.getLoginId();

        boolean exist = redisUtil.checkExistSetItem(USER_LIKE_VIDEO + loginId, videoInfoVo.getVideoInfoId());

        if (exist){
            videoInfoVo.setLiked(true);
            videoInfoVo.setLikeCount(videoInfoVo.getLikeCount()+1);
        }else {
            videoInfoVo.setLiked(false);
        }
    }

    ;

    @Override
    public Long updateLikeCount(String videoInfoId) {
        String refreshkey = "refreshVideoLike:" + videoInfoId;

        Integer likeCount = (Integer) redisUtil.get(VIDEO_LIKE_COUNT+videoInfoId);

        if(likeCount == null){
            redisUtil.set(VIDEO_LIKE_COUNT +videoInfoId,0);
            likeCount = 0;
        }

        refreshDataService.refreshData(likeCount,refreshkey,(count) -> {
            videoinfoService.update(
                    new LambdaUpdateWrapper<Videoinfo>()
                            .eq(Videoinfo::getId, videoInfoId)
                            .set(Videoinfo::getLikeCount, count));
        });

        return Long.valueOf(likeCount);
    }







}
