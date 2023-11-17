package com.ducksoup.dilivideocontent.mainservices.UserOperation.Impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ducksoup.dilivideocontent.aop.annonation.PerformanceLog;
import com.ducksoup.dilivideocontent.entity.Videoinfo;
import com.ducksoup.dilivideocontent.mainservices.UserOperation.RefreshDataService;
import com.ducksoup.dilivideocontent.mainservices.UserOperation.ViewsService;
import com.ducksoup.dilivideocontent.service.VideoinfoService;
import com.ducksoup.dilivideocontent.utils.RedisUtil;
import com.ducksoup.dilivideoentity.vo.VideoInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 视频播放量相关操作
 */
@Service
public class ViewsServiceImpl implements ViewsService {


    //观看量前缀
    private final String VIDEO_VIEW_PREFIX = "VIDEO_VIEW_PREFIX:";

    //观看量锁
    private final String VIDEO_VIEW_LOCK = "VIDEO_VIEW_LOCK:";

    @Autowired
    private VideoinfoService videoinfoService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RefreshDataService refreshDataService;

    /**
     * 增加播放量 +1
     * @param videoInfoId videoInfoId
     * @return watch count (Long)
     */
    @Override
    public Long addViews(String videoInfoId) {

        String key = VIDEO_VIEW_PREFIX+videoInfoId;

        if (!redisUtil.exists(key)){
            redisUtil.set(key,0);
        }

        Long views = redisUtil.increaseKey(key);


        String lock = VIDEO_VIEW_LOCK+videoInfoId;

        refreshDataService.refreshData(views,lock,(view) -> {
            videoinfoService.update(
                    new LambdaUpdateWrapper<Videoinfo>()
                            .eq(Videoinfo::getId, videoInfoId)
                            .set(Videoinfo::getWatchCount, view));
        });

        return views;


    }


    /**
     * 设置视频播放量
     * @param videoInfoVo VideoInfoVo
     */
    @Override
    public void setVideoViewCount(VideoInfoVo videoInfoVo) {
        String key = VIDEO_VIEW_PREFIX+videoInfoVo.getVideoInfoId();

        if (!redisUtil.exists(key)){
            redisUtil.set(key,0);
        }
        videoInfoVo.setWatchCount(Long.valueOf(redisUtil.get(key).toString()));
    }



    /**
     * 设置视频播放量
     * @param videoInfoVos List<VideoInfoVo>
     */
    @Override
    public void setVideoListViewCount(List<VideoInfoVo> videoInfoVos) {


        List<String> keys = videoInfoVos.stream().map(item->VIDEO_VIEW_PREFIX+item.getVideoInfoId()).collect(Collectors.toList());
        List<Integer> list = redisUtil.mget(keys);

        Map<String, Serializable> map = new HashMap<>();

        for (int i = 0; i < videoInfoVos.size(); i++) {
            VideoInfoVo videoInfoVo = videoInfoVos.get(i);
            Integer viewCount = list.get(i);

            if (viewCount==null){
                map.put(keys.get(i),viewCount);
                viewCount = 0;
            }
            videoInfoVo.setWatchCount(Long.valueOf(viewCount));
        }

        redisUtil.mset(map);



    }
}
