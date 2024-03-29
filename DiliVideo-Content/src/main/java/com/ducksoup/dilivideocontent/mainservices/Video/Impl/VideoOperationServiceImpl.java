package com.ducksoup.dilivideocontent.mainservices.Video.Impl;


import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.ducksoup.dilivideocontent.controller.Params.VideoInfoForm;
import com.ducksoup.dilivideocontent.entity.*;
import com.ducksoup.dilivideocontent.mainservices.MinIO.DeleteService;
import com.ducksoup.dilivideocontent.mainservices.MinIO.UploadService;
import com.ducksoup.dilivideocontent.mainservices.Video.VideoOperationService;
import com.ducksoup.dilivideocontent.service.CoverService;
import com.ducksoup.dilivideocontent.service.VideofileService;
import com.ducksoup.dilivideocontent.service.VideoinfoService;

import com.ducksoup.dilivideocontent.utils.RedisUtil;
import com.ducksoup.dilivideoentity.auth.MUser;
import com.ducksoup.dilivideoentity.constant.CONSTANT_STATUS;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import com.ducksoup.dilivideofeign.auth.AuthServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class VideoOperationServiceImpl implements VideoOperationService {
    @Autowired
    private VideofileService videofileService;

    @Autowired
    private VideoinfoService videoinfoService;


    @Autowired
    private UploadService uploadService;


    /**
     * feign：AuthServices
     */
    @Autowired
    private AuthServices authServices;

    @Autowired
    private DeleteService deleteService;

    @Autowired
    private CoverService coverService;

    @Autowired
    private RedisUtil redisUtil;


    @Override
    public boolean saveVideo(VideoInfoForm videoInfoForm, Videofile videofile) {


        String loginId = (String) StpUtil.getLoginId();
        ResponseResult<MUser> userInfo = authServices.getUserInfo(loginId);
        MUser user = userInfo.getData();
        DateTime now = DateTime.now();

        //videoinfoId
        String infoId = UUID.randomUUID().toString();


        //填充视频基本信息
        Videoinfo videoinfo = new Videoinfo();
        videoinfo.setId(infoId);
        videoinfo.setTitle(videoInfoForm.getTitle());
        videoinfo.setAuthorid(loginId);
        videoinfo.setSummary(videoInfoForm.getDescription());
        videoinfo.setCreateTime(now);
        videoinfo.setUpdateTime(now);
        videoinfo.setWatchCount(0L);
        videoinfo.setLikeCount(0L);
        videoinfo.setCollectCount(0);
        videoinfo.setCommentCount(0);
        videoinfo.setMarkStatus(CONSTANT_STATUS.VIDEO_STATUS_HANDLING);
        videoinfo.setStatus(1);
        videoinfo.setIsPublish(1);
        videoinfo.setOpenComment(1);
        videoinfo.setVideofileId(videofile.getId());
        videoinfo.setIsOriginal(videoInfoForm.isIforiginal() ? 1 : 0);
        videoinfo.setAuthorName(user.getNickname());
        videoinfo.setPartitionId(videoInfoForm.getPartitionId());

        try {
            String coverid = uploadService.uploadCover(videoInfoForm.getFile());
            videoinfo.setCoverId(coverid);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        boolean videoinfosave = videoinfoService.save(videoinfo);
        videofile.setVideoinfoId(infoId);
        //保存文件信息
        boolean videofilesave = videofileService.save(videofile);

        boolean saveVideoTagInfo = false;
        if (videoInfoForm.getTagIds() != null) {
            saveVideoTagInfo = saveVideoTagInfo(infoId, videoInfoForm.getTagIds());
        }

        return videoinfosave && videofilesave && saveVideoTagInfo;

    }



    /**
     * @Author ducksoup
     * 删除视频（全面删除）
     * @param  videoInfoId 视频infoID
     * @return bool
     */

    @Override
    public boolean deleteVideo(String videoInfoId) {


        Videoinfo videoinfo = videoinfoService.getById(videoInfoId);

        if (videoinfo==null){
            return false;
        }

        Videofile videofile = videofileService.getById(videoinfo.getVideofileId());
        Cover cover = coverService.getById(videoinfo.getCoverId());

        //mp4路径
        String mp4 = videofile.getPath().split("\\.")[0]+".mp4";

        log.info("删除文件"+videoInfoId);
        boolean a = deleteService.deleteObj(mp4,"video");
        boolean b = deleteService.deleteObj(videofile.getPath(),"video");
        boolean c = deleteService.deleteObj(cover.getPath(),"img");


        redisUtil.remove("VideoPlayUrl:"+videofile.getId());


        //删除数据库中videofile和cover的记录
        boolean videoFileRemove = videofileService.removeById(videoinfo.getVideofileId());
        boolean coverRemove = coverService.removeById(videoinfo.getCoverId());
        videoinfoService.removeById(videoInfoId);



        return a&&b&&c&&videoFileRemove&&coverRemove;
    }

    @Override
    public boolean saveVideoTagInfo(String videoInfoId, List<String> tagIds) {

        List<Tag> tags = Db.lambdaQuery(Tag.class)
                .in(Tag::getId, tagIds)
                .select(Tag::getId).select(Tag::getTagNo).list();

        List<VideoinfoTag> tagMappings = new ArrayList<>();

        tags.forEach(tag -> {
            VideoinfoTag tagMapping = new VideoinfoTag();
            tagMapping.setVideoInfoId(videoInfoId);
            tagMapping.setTagId(tag.getId());
            tagMapping.setTagNo(tag.getTagNo());
            DateTime now = DateTime.now();
            tagMapping.setCreateTime(now);
            tagMapping.setUpdateTime(now);
            tagMappings.add(tagMapping);
        });

        return Db.saveBatch(tagMappings);
    }
}