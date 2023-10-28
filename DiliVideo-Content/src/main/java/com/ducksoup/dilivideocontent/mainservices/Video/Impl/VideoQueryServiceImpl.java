package com.ducksoup.dilivideocontent.mainservices.Video.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ducksoup.dilivideocontent.entity.Videoinfo;
import com.ducksoup.dilivideocontent.mainservices.UserOperation.LikeOperationService;
import com.ducksoup.dilivideocontent.mainservices.UserOperation.ViewsService;
import com.ducksoup.dilivideocontent.mainservices.Video.VideoQueryService;
import com.ducksoup.dilivideocontent.service.CoverService;
import com.ducksoup.dilivideocontent.service.VideoinfoService;
import com.ducksoup.dilivideoentity.vo.VideoInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class VideoQueryServiceImpl implements VideoQueryService {

    @Autowired
    private VideoinfoService videoinfoService;

    @Autowired
    private CoverService coverService;

    @Autowired
    private ViewsService viewsService;

    @Resource
    private LikeOperationService likeOperationService;

    @Cacheable(cacheNames = "videoRecommend",key = "#page")
    @Override
    public List<VideoInfoVo> getRecommend(Integer page) {

        int pageSize = 10;

        Page<Videoinfo> pager = new Page<>(page, pageSize);

        videoinfoService.page(pager, new LambdaQueryWrapper<Videoinfo>().eq(Videoinfo::getStatus, 1).orderByDesc(Videoinfo::getCreateTime));

        List<Videoinfo> videoinfos = pager.getRecords();

        List<VideoInfoVo> videoInfoVos = videoinfoService.getVideoInfoVoByVideoInfo(videoinfos);

        viewsService.setVideoListViewCount(videoInfoVos);
        likeOperationService.setVideoLikeStatus(videoInfoVos);

        return videoInfoVos;

    }
}
