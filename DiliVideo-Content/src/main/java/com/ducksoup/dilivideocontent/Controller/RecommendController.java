package com.ducksoup.dilivideocontent.Controller;
import java.util.Date;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ducksoup.dilivideocontent.Entity.Cover;
import com.ducksoup.dilivideocontent.Entity.Videoinfo;
import com.ducksoup.dilivideocontent.service.CoverService;
import com.ducksoup.dilivideocontent.service.VideoinfoService;
import com.ducksoup.dilivideoentity.Result.ResponseResult;
import com.ducksoup.dilivideoentity.vo.VideoInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/recommend")
public class RecommendController {

    @Autowired
    private VideoinfoService videoinfoService;

    @Autowired
    private CoverService coverService;



    //Todo 缓存优化
    @SaCheckLogin
    @GetMapping("/latest")
    public ResponseResult<List<VideoInfoVo>> getLatestVideo(@RequestParam Integer page){

        int pageSize = 10;

        Page<Videoinfo> pager = new Page<>(page,pageSize);

        videoinfoService.page(pager,new LambdaQueryWrapper<Videoinfo>().eq(Videoinfo::getStatus,1).orderByDesc(Videoinfo::getCreateTime));

        List<Videoinfo> videoinfos = pager.getRecords();

        List<VideoInfoVo> videoInfoVos = videoinfoService.getVideoInfoVoByVideoInfo(videoinfos);

        return new ResponseResult<>(HttpStatus.HTTP_OK,"success",videoInfoVos);

    }


}
