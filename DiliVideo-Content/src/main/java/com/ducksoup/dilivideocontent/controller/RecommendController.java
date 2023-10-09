package com.ducksoup.dilivideocontent.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ducksoup.dilivideocontent.entity.Videoinfo;
import com.ducksoup.dilivideocontent.mainservices.UserOperation.ViewsService;
import com.ducksoup.dilivideocontent.service.CoverService;
import com.ducksoup.dilivideocontent.service.VideoinfoService;
import com.ducksoup.dilivideocontent.utils.TimeUtils;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import com.ducksoup.dilivideoentity.vo.VideoInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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


    //Todo 缓存优化
    @SaCheckLogin
    @GetMapping("/latest")
    public ResponseResult<List<VideoInfoVo>> getLatestVideo(@RequestParam Integer page) {

        int pageSize = 10;

        Page<Videoinfo> pager = new Page<>(page, pageSize);

        videoinfoService.page(pager, new LambdaQueryWrapper<Videoinfo>().eq(Videoinfo::getStatus, 1).orderByDesc(Videoinfo::getCreateTime));

        List<Videoinfo> videoinfos = pager.getRecords();

        List<VideoInfoVo> videoInfoVos = videoinfoService.getVideoInfoVoByVideoInfo(videoinfos);

        viewsService.setVideoListViewCount(videoInfoVos);


        return new ResponseResult<>(HttpStatus.HTTP_OK, "success", videoInfoVos);

    }


    @SaCheckLogin
    @GetMapping("/hot")
    public ResponseResult<List<VideoInfoVo>> getHotVideo(@RequestParam Integer page) {
        int pageSize = 10;
        Page<Videoinfo> pager = new Page<>(page, pageSize);


        DateTime now = DateTime.now();



        //get start date of current month
        LocalDate start = TimeUtils.getStartOfCurrentWeek();
        LocalDate end = TimeUtils.getEndOfCurrentWeek();


        videoinfoService.page(pager, new LambdaQueryWrapper<Videoinfo>().eq(Videoinfo::getStatus, 1).between(Videoinfo::getCreateTime, start, end).orderByDesc(Videoinfo::getWatchCount));

        List<Videoinfo> records = pager.getRecords();

        if (records.isEmpty()){
            videoinfoService.page(pager, new LambdaQueryWrapper<Videoinfo>().eq(Videoinfo::getStatus, 1).orderByDesc(Videoinfo::getWatchCount));
            records = pager.getRecords();
        }

        List<VideoInfoVo> videoInfoVos = videoinfoService.getVideoInfoVoByVideoInfo(records);

        viewsService.setVideoListViewCount(videoInfoVos);

        return new ResponseResult<>(HttpStatus.HTTP_OK, "success", videoInfoVos);


    }


}
