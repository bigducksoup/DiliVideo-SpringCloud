package com.ducksoup.dilivideocontent.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.exception.NotLoginException;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ducksoup.dilivideocontent.entity.Cover;
import com.ducksoup.dilivideocontent.entity.Videofile;
import com.ducksoup.dilivideocontent.entity.Videoinfo;
import com.ducksoup.dilivideocontent.service.CoverService;
import com.ducksoup.dilivideocontent.service.VideofileService;
import com.ducksoup.dilivideocontent.service.VideoinfoService;
import com.ducksoup.dilivideocontent.utils.OSSUtils;
import com.ducksoup.dilivideoentity.auth.MUser;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import com.ducksoup.dilivideoentity.vo.UserVo;
import com.ducksoup.dilivideoentity.vo.VideoFileVo;
import com.ducksoup.dilivideoentity.vo.VideoInfoVo;
import com.ducksoup.dilivideofeign.auth.AuthServices;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 与视频信息相关的Api
 */

@RestController
@RequestMapping("/video_info")
public class VideoInfoController {

    @Autowired
    private VideofileService videofileService;

    @Autowired
    private AuthServices authServices;

    @Autowired
    private VideoinfoService videoinfoService;

    @Autowired
    private CoverService coverService;


    @Autowired
    private OSSUtils ossUtils;

    /**
     * 获取视频播放url
     * @param videoId
     * @return
     */
    @SaCheckLogin
    @GetMapping("/get_playurl")
    @Cacheable(cacheNames = "VideoPlayUrl",key = "#videoId")
    public ResponseResult<VideoFileVo> getPlayerUrl(@RequestParam String videoId){
        Videofile videofile = videofileService.getById(videoId);

        if (videofile==null){
            return new ResponseResult<>(HttpStatus.HTTP_NOT_FOUND,"未找到资源");
        }

        VideoFileVo videoFileVo = new VideoFileVo();
        BeanUtils.copyProperties(videofile,videoFileVo);

        String url = ossUtils.makeUrl(videofile.getBucket(), videofile.getPath());
        videoFileVo.setFullpath(url);

        return new ResponseResult<>(HttpStatus.HTTP_OK,"获取视频播放信息",videoFileVo);
    }

    /**
     * 获取视频作者信息
     * @param authorId
     * @return
     */

    @SaCheckLogin
    @GetMapping("/get_authorInfo")
    public ResponseResult<UserVo> getAutherInfo(@RequestParam String authorId){
        ResponseResult<MUser> result = authServices.getUserInfo(authorId);
        if (result.getCode()!=200){
            throw new NotLoginException("","","");
        }
        MUser data = result.getData();
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(data,userVo);
        return new ResponseResult<>(HttpStatus.HTTP_OK,"获取用户信息成功",userVo);
    }

    /**
     * 获取视频信息
     * @param videoInfoId
     * @return
     */

    @SaCheckLogin
    @GetMapping("/get_videoInfo_byId")
    public ResponseResult<VideoInfoVo> getVideoInfoVoById(@RequestParam String videoInfoId){

        Videoinfo videoinfo = videoinfoService.getById(videoInfoId);

        Cover cover = coverService.getById(videoinfo.getCoverId());


        VideoInfoVo videoInfoVo = new VideoInfoVo();

        videoInfoVo.setVideoInfoId(videoinfo.getId());
        videoInfoVo.setVideoAuthorId(videoinfo.getAuthorid());
        videoInfoVo.setVideoAuthorName(videoinfo.getAuthorName());
        videoInfoVo.setCollectCount(videoinfo.getCollectCount());
        videoInfoVo.setCommentCount(videoinfo.getCommentCount());
        videoInfoVo.setCreateTime(videoinfo.getCreateTime());
        videoInfoVo.setIsOriginal(videoinfo.getIsOriginal());
        videoInfoVo.setWatchCount(videoinfo.getWatchCount());
        videoInfoVo.setLikeCount(videoinfo.getLikeCount());
        videoInfoVo.setIsPublish(videoinfo.getIsPublish());
        videoInfoVo.setOpenComment(videoinfo.getOpenComment());
        videoInfoVo.setTitle(videoinfo.getTitle());
        videoInfoVo.setSummary(videoinfo.getSummary());
        videoInfoVo.setVideoFileId(videoinfo.getVideofileId());
        videoInfoVo.setVideoFileUrl("null");
        videoInfoVo.setVideoFileName("null");
        videoInfoVo.setCoverId(cover.getId());
        videoInfoVo.setCoverName(cover.getUniqueName());

        String coverUrl = ossUtils.makeUrl(cover.getBucket(), cover.getPath());

        videoInfoVo.setCoverUrl(coverUrl);
        videoInfoVo.setPartitionId(videoinfo.getPartitionId());


        return new ResponseResult<>(HttpStatus.HTTP_OK,"查询成功",videoInfoVo);


    }


    @GetMapping("/get_videoInfo_byIdList")
    public ResponseResult<List<VideoInfoVo>> getVideoInfoVoByIds(@RequestBody List<String> ids){


        List<Videoinfo> videoinfos = videoinfoService.list(new LambdaQueryWrapper<Videoinfo>()
                .in(Videoinfo::getId, ids)
        );

        List<VideoInfoVo> videoInfoVos = videoinfoService.getVideoInfoVoByVideoInfo(videoinfos);

        return new ResponseResult<>(HttpStatus.HTTP_OK,"查询成功",videoInfoVos);
    }





}
