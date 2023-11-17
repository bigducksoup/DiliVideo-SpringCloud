package com.ducksoup.dilivideocontent.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.date.DateTime;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ducksoup.dilivideocontent.entity.Videoinfo;
import com.ducksoup.dilivideocontent.mainservices.UserOperation.LikeOperationService;
import com.ducksoup.dilivideocontent.mainservices.UserOperation.ViewsService;
import com.ducksoup.dilivideocontent.mainservices.Video.VideoQueryService;
import com.ducksoup.dilivideocontent.service.CoverService;
import com.ducksoup.dilivideocontent.service.VideoinfoService;
import com.ducksoup.dilivideocontent.utils.TimeUtils;
import com.ducksoup.dilivideoentity.constant.CONSTANT_STATUS;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import com.ducksoup.dilivideoentity.vo.VideoInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/recommend")
public class RecommendController {

    @Autowired
    private VideoinfoService videoinfoService;

    @Autowired
    private CoverService coverService;

    @Autowired
    private ViewsService viewsService;


    @Resource
    private VideoQueryService videoQueryService;

    @Resource
    private LikeOperationService likeOperationService;


    //Todo 缓存优化
    @SaCheckLogin
    @GetMapping("/latest")
    public ResponseResult<List<VideoInfoVo>> getLatestVideo(@RequestParam Integer page) {

        return new ResponseResult<>(HttpStatus.HTTP_OK, "success", videoQueryService.getRecommend(page));

    }


    @SaCheckLogin
    @GetMapping("/hot")
    @Cacheable(cacheNames = "hotVideo",key = "#page")
    public ResponseResult<List<VideoInfoVo>> getHotVideo(@RequestParam Integer page) {
        int pageSize = 10;
        Page<Videoinfo> pager = new Page<>(page, pageSize);


        DateTime now = DateTime.now();



        //get start date of current month
        LocalDate start = TimeUtils.getStartOfCurrentWeek();
        LocalDate end = TimeUtils.getEndOfCurrentWeek();


        videoinfoService.page(pager, new LambdaQueryWrapper<Videoinfo>().eq(Videoinfo::getStatus, 1).eq(Videoinfo::getMarkStatus, CONSTANT_STATUS.VIDEO_STATUS_READY).between(Videoinfo::getCreateTime, start, end).orderByDesc(Videoinfo::getWatchCount));

        List<Videoinfo> records = pager.getRecords();

        if (records.isEmpty()){
            videoinfoService.page(pager, new LambdaQueryWrapper<Videoinfo>().eq(Videoinfo::getStatus, 1).eq(Videoinfo::getMarkStatus, CONSTANT_STATUS.VIDEO_STATUS_READY).orderByDesc(Videoinfo::getWatchCount));
            records = pager.getRecords();
        }

        List<VideoInfoVo> videoInfoVos = videoinfoService.getVideoInfoVoByVideoInfo(records);

        viewsService.setVideoListViewCount(videoInfoVos);
        likeOperationService.setVideoLikeStatus(videoInfoVos);

        return new ResponseResult<>(HttpStatus.HTTP_OK, "success", videoInfoVos);


    }


    /**
     * 获取视频相关的推荐
     * @param videoInfoId 视频信息id
     * @return List<VideoInfoVo>
     *   TODO test
     */
    @GetMapping("/related_video")
    public ResponseResult<List<VideoInfoVo>> getRelatedVideo(@RequestParam String videoInfoId){

        List<VideoInfoVo> relatedVideo = videoQueryService.getRelatedVideo(videoInfoId);

        return new ResponseResult<>(HttpStatus.HTTP_OK,"查询成功",relatedVideo);

    }





}
