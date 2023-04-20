package com.ducksoup.dilivideocontent.Controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.exception.NotLoginException;
import cn.hutool.http.HttpStatus;
import com.ducksoup.dilivideocontent.Entity.Cover;
import com.ducksoup.dilivideocontent.Entity.Videofile;
import com.ducksoup.dilivideocontent.Entity.Videoinfo;
import com.ducksoup.dilivideocontent.service.CoverService;
import com.ducksoup.dilivideocontent.service.VideofileService;
import com.ducksoup.dilivideocontent.service.VideoinfoService;
import com.ducksoup.dilivideoentity.AuthEntity.MUser;
import com.ducksoup.dilivideoentity.Result.ResponseResult;
import com.ducksoup.dilivideoentity.vo.UserVo;
import com.ducksoup.dilivideoentity.vo.VideoFileVo;
import com.ducksoup.dilivideoentity.vo.VideoInfoVo;
import com.ducksoup.dilivideofeign.Auth.AuthServices;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


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
    /**
     * 获取视频播放url
     * @param videoId
     * @return
     */
    @SaCheckLogin
    @GetMapping("/get_playurl")
    public ResponseResult<VideoFileVo> getPlayerUrl(@RequestParam String videoId){
        Videofile videofile = videofileService.getById(videoId);
        VideoFileVo videoFileVo = new VideoFileVo();
        BeanUtils.copyProperties(videofile,videoFileVo);
        return new ResponseResult<>(HttpStatus.HTTP_OK,"获取视频播放信息",videoFileVo);
    }

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
        videoInfoVo.setCoverUrl(cover.getFullpath());
        videoInfoVo.setPartitionId(videoinfo.getPartitionId());


        return new ResponseResult<>(HttpStatus.HTTP_OK,"查询成功",videoInfoVo);


    }


}
