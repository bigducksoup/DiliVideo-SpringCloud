package com.ducksoup.dilivideocontent.mainservices.Video.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ducksoup.dilivideobase.annotation.Cache;
import com.ducksoup.dilivideocontent.entity.Videoinfo;
import com.ducksoup.dilivideocontent.mainservices.UserOperation.LikeOperationService;
import com.ducksoup.dilivideocontent.mainservices.UserOperation.ViewsService;
import com.ducksoup.dilivideocontent.mainservices.Video.VideoQueryService;
import com.ducksoup.dilivideocontent.mapper.VideoinfoMapper;
import com.ducksoup.dilivideocontent.service.VideoinfoService;
import com.ducksoup.dilivideocontent.utils.OSSUtils;
import com.ducksoup.dilivideoentity.constant.CONSTANT_STATUS;
import com.ducksoup.dilivideoentity.vo.VideoInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class VideoQueryServiceImpl implements VideoQueryService {

    @Autowired
    private VideoinfoService videoinfoService;

    @Resource
    private VideoinfoMapper videoinfoMapper;

    @Autowired
    private ViewsService viewsService;

    @Resource
    private LikeOperationService likeOperationService;

    @Resource
    private OSSUtils ossUtils;


    @Override
    public List<VideoInfoVo> getRecommend(Integer page) {

        int pageSize = 10;

        Page<Videoinfo> pager = new Page<>(page, pageSize);

        videoinfoService.page(pager, new LambdaQueryWrapper<Videoinfo>().eq(Videoinfo::getStatus, 1).eq(Videoinfo::getMarkStatus, CONSTANT_STATUS.VIDEO_STATUS_READY).orderByDesc(Videoinfo::getCreateTime));

        List<Videoinfo> videoinfos = pager.getRecords();

        List<VideoInfoVo> videoInfoVos = videoinfoService.getVideoInfoVoByVideoInfo(videoinfos);

        viewsService.setVideoListViewCount(videoInfoVos);
        likeOperationService.setVideoLikeStatus(videoInfoVos);



        return videoInfoVos;

    }



    @Cache(cacheName = "VIDEO_RELATED", key = "#videoInfoId",timeUnit = TimeUnit.MINUTES,expireTime = 10)
    @Override
    public List<VideoInfoVo> getRelatedVideo(String videoInfoId) {
        List<Videoinfo> videoinfos = videoinfoMapper.queryRelatedVideo(videoInfoId);

        return videoinfoService.getVideoInfoVoByVideoInfo(videoinfos);

    }






}
