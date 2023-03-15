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

        List<VideoInfoVo> videoInfoVos = new ArrayList<>();

        videoinfos.forEach((item)->{
            Cover cover = coverService.getOne(new LambdaQueryWrapper<Cover>().eq(Cover::getId, item.getCoverId()));
            VideoInfoVo videoInfoVo = new VideoInfoVo();
            videoInfoVo.setVideoInfoId(item.getId());
            videoInfoVo.setVideoAuthorName(item.getAuthorName());
            videoInfoVo.setVideoAuthorId(item.getAuthorid());
            videoInfoVo.setCollectCount(item.getCollectCount());
            videoInfoVo.setCommentCount(item.getCommentCount());
            videoInfoVo.setCreateTime(item.getCreateTime());
            videoInfoVo.setIsOriginal(item.getIsOriginal());
            videoInfoVo.setWatchCount(item.getWatchCount());
            videoInfoVo.setLikeCount(item.getLikeCount());
            videoInfoVo.setIsPublish(item.getIsPublish());
            videoInfoVo.setOpenComment(item.getOpenComment());
            videoInfoVo.setTitle(item.getTitle());
            videoInfoVo.setSummary(item.getSummary());
            videoInfoVo.setVideoFileId(item.getVideofileId());
            videoInfoVo.setVideoFileUrl("null");
            videoInfoVo.setVideoFileName("null");
            videoInfoVo.setCoverId(item.getCoverId());
            videoInfoVo.setCoverName(cover.getUniqueName());
            videoInfoVo.setCoverUrl(cover.getFullpath());
            videoInfoVo.setVideoFileId(item.getVideofileId());
            videoInfoVos.add(videoInfoVo);
        });

        return new ResponseResult<>(HttpStatus.HTTP_OK,"success",videoInfoVos);

    }


}
