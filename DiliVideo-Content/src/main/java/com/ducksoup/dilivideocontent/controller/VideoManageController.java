package com.ducksoup.dilivideocontent.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ducksoup.dilivideocontent.controller.Params.ChunkParams;
import com.ducksoup.dilivideocontent.controller.Params.VideoDeleteParams;
import com.ducksoup.dilivideocontent.controller.Params.VideoInfoForm;
import com.ducksoup.dilivideocontent.controller.Params.VideoInfoUpdateForm;
import com.ducksoup.dilivideocontent.entity.VideoChunk;
import com.ducksoup.dilivideocontent.entity.Videofile;
import com.ducksoup.dilivideocontent.entity.Videoinfo;
import com.ducksoup.dilivideocontent.dto.FileSavedInfo;
import com.ducksoup.dilivideocontent.dto.FileTransmissionInfo;
import com.ducksoup.dilivideocontent.mainservices.MinIO.FileCombineService;
import com.ducksoup.dilivideocontent.mainservices.MinIO.MinIOImpl.UploadServiceImpl;
import com.ducksoup.dilivideocontent.mainservices.Rabbit.NotifyVideoFFMPEG;
import com.ducksoup.dilivideocontent.mainservices.Video.VideoOperationService;
import com.ducksoup.dilivideocontent.service.CoverService;
import com.ducksoup.dilivideocontent.service.VideoChunkService;
import com.ducksoup.dilivideocontent.service.VideofileService;
import com.ducksoup.dilivideocontent.service.VideoinfoService;
import com.ducksoup.dilivideocontent.utils.RedisUtil;
import com.ducksoup.dilivideoentity.constant.CONSTANT_MinIO;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import com.ducksoup.dilivideoentity.vo.VideoInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/video")
public class VideoManageController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UploadServiceImpl uploadServiceImpl;

    @Autowired
    private VideoOperationService videoOperationService;

    @Autowired
    private VideoinfoService videoinfoService;

    @Autowired
    private FileCombineService fileCombineService;

    @Autowired
    private NotifyVideoFFMPEG notifyVideoFFMPEG;

    @Autowired
    private VideoChunkService videoChunkService;


    @Autowired
    private VideofileService videofileService;

    @Autowired
    private CoverService coverService;



    @SaCheckLogin
    @PostMapping("/upload")
    public ResponseResult<String> uploadVideo(FileTransmissionInfo fileTransmissionInfo) {

        try {
            String s = uploadServiceImpl.uploadVideo(fileTransmissionInfo.getFile(), fileTransmissionInfo.getCode());
        } catch (Exception e) {
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR, "上传失败");
        }
        return new ResponseResult<>(HttpStatus.HTTP_OK, "上传成功", fileTransmissionInfo.getCode());
    }


    @SaCheckLogin
    @PostMapping("/check_md5_upload")
    public ResponseResult<Boolean> checkMd5Upload(String md5,String code) {

        VideoChunk videoChunk = videoChunkService.checkExist(md5);
        if (videoChunk==null){
            return new ResponseResult<>(HttpStatus.HTTP_OK,"not exist",false);
        }

        redisUtil.addToSet(CONSTANT_MinIO.VIDEO_CHUNK_LIST_PREFIX + code, videoChunk);
        redisUtil.addToSet(CONSTANT_MinIO.VIDEO_CHUNK_MD5_PREFIX+code,videoChunk.getMd5());


        return  new ResponseResult<>(HttpStatus.HTTP_OK,"exist",true);



    }


    @SaCheckLogin
    @PostMapping("/upload_chunk")
    public ResponseResult<Void> uploadChunk(ChunkParams params) {

        //获取登录id
        String loginId = (String) StpUtil.getLoginId();

        if ( redisUtil.get(CONSTANT_MinIO.RANDOM_CODE_CHECK_PREFIX + params.getCode()) == null || !redisUtil.get(CONSTANT_MinIO.RANDOM_CODE_CHECK_PREFIX + params.getCode()).equals(loginId)) {
            return new ResponseResult<>(HttpStatus.HTTP_FORBIDDEN, "code无效");
        }

        log.info("接收第"+params.getIndex()+"片文件，共有"+params.getTotalChunkCount()+"片");

        try {
            String md5Hex = DigestUtil.md5Hex(params.getFile().getInputStream());
            VideoChunk videoChunk = videoChunkService.checkExist(md5Hex);

            if (videoChunk == null) {
                log.info("数据库不存在第"+params.getIndex()+"片"+" code="+params.getCode());
                FileSavedInfo savedInfo = uploadServiceImpl.uploadFile(params.getFile(), CONSTANT_MinIO.VIDEO_CHUNK_BUCKET, md5Hex);
                videoChunk = new VideoChunk();
                videoChunk.setId(UUID.randomUUID().toString());
                videoChunk.setUniqueName(savedInfo.getFileName());
                videoChunk.setOriginalName(params.getFile().getOriginalFilename());
                videoChunk.setCode(params.getCode());
                videoChunk.setUploadTime(DateTime.now());
                videoChunk.setMd5(md5Hex);
                videoChunk.setChunkBucket(CONSTANT_MinIO.VIDEO_CHUNK_BUCKET);
                videoChunk.setChunkPath(savedInfo.getPath());
                videoChunk.setUsed(0);
                videoChunk.setTotalChunkCount(params.getTotalChunkCount());
                videoChunk.setChunkIndex(params.getIndex());
                videoChunkService.save(videoChunk);
            }

            if (!redisUtil.checkExistSetItem(CONSTANT_MinIO.VIDEO_CHUNK_MD5_PREFIX+params.getCode(),videoChunk.getMd5())){
                log.info("redis不存在第"+params.getIndex()+"片"+" code="+params.getCode());
                redisUtil.addToSet(CONSTANT_MinIO.VIDEO_CHUNK_LIST_PREFIX + params.getCode(), videoChunk);
                redisUtil.addToSet(CONSTANT_MinIO.VIDEO_CHUNK_MD5_PREFIX+params.getCode(),videoChunk.getMd5());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR,"上传失败");
        }



        //加锁 防止 在删除set前 线程同时进入if语句造成合并多次
        if (redisUtil.countSetItem(CONSTANT_MinIO.VIDEO_CHUNK_MD5_PREFIX + params.getCode()) == params.getTotalChunkCount() && redisUtil.setNx("lock:" + params.getCode(), 100)){
            redisUtil.remove(CONSTANT_MinIO.VIDEO_CHUNK_MD5_PREFIX + params.getCode());
            redisUtil.remove(CONSTANT_MinIO.RANDOM_CODE_CHECK_PREFIX + params.getCode());

            Set<VideoChunk> chunks = redisUtil.getSet(CONSTANT_MinIO.VIDEO_CHUNK_LIST_PREFIX + params.getCode(), VideoChunk.class);
            log.info("开始合并文件 code="+params.getCode());
            //异步合并文件
            new Thread(()->{
                fileCombineService.combineVideoChunks(chunks,params.getFileName(),params.getCode());
            }).start();

            //解锁
            redisUtil.remove("lock:" + params.getCode());
        }

        return new ResponseResult<>(HttpStatus.HTTP_OK,"上传成功");

    }


    @SaCheckLogin
    @GetMapping("/getuploadcode")
    public ResponseResult<String> getUploadCode() {

        //获取登录id
        String loginId = (String) StpUtil.getLoginId();

        String randomNumber = RandomUtil.randomNumbers(15);

        while (redisUtil.exists(randomNumber)) {
            randomNumber = RandomUtil.randomNumbers(15);
        }

        redisUtil.set(CONSTANT_MinIO.RANDOM_CODE_CHECK_PREFIX + randomNumber, loginId, 24L, TimeUnit.HOURS);

        return new ResponseResult<String>(HttpStatus.HTTP_OK, "获取code成功", randomNumber);
    }

    @PostMapping("/submit_videoInfo_form")
    public ResponseResult<Boolean> submitVideoInfoForm(@Validated VideoInfoForm videoInfoForm) {

        String code = videoInfoForm.getCode();
        //通过code防止重复提交，且取出上传视频时保存的数据
        Videofile fileinfo = (Videofile) redisUtil.get(code);
        if (fileinfo == null) {
            return new ResponseResult<>(HttpStatus.HTTP_GONE, "code无效");
        }
        redisUtil.remove(code);

        //保存视频信息，与上传封面
        boolean saveVideo = videoOperationService.saveVideo(videoInfoForm, fileinfo);

        if (saveVideo) {
            notifyVideoFFMPEG.notifyVideoFFMPG(fileinfo);
        }

        return saveVideo ? new ResponseResult<>(HttpStatus.HTTP_OK, "上传完成", true) :
                new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR, "上传失败", false);
    }


    /**
     * 删除视频
     *
     * @param params 视频信息Id
     * @return ResponseResult<Boolean>
     */
    @SaCheckLogin
    @PostMapping("/delete")
    public ResponseResult<Boolean> deleteVideo(@RequestBody VideoDeleteParams params) {


        String loginId = (String) StpUtil.getLoginId();
        Videoinfo videoInfoUserId = videoinfoService.getOne(new LambdaQueryWrapper<Videoinfo>().select(Videoinfo::getAuthorid).eq(Videoinfo::getId, params.getVideoInfoId()));

        if (!loginId.equals(videoInfoUserId.getAuthorid())) {
            return new ResponseResult<>(HttpStatus.HTTP_FORBIDDEN, "权限不足", false);
        }


        boolean b = videoOperationService.deleteVideo(params.getVideoInfoId());

        if (b) {
            return new ResponseResult<>(HttpStatus.HTTP_OK, "删除成功", true);
        } else {
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR, "删除失败", false);
        }

    }


    @SaCheckLogin
    @GetMapping("/published_videos")
    public ResponseResult<List<VideoInfoVo>> getOwnVideoList(@RequestParam Integer page) {

        String loginId = (String) StpUtil.getLoginId();

        int pageSize = 10;

        List<VideoInfoVo> videoInfoVos = videoinfoService.getPublishedVideoById(loginId, page, pageSize);

        return new ResponseResult<>(HttpStatus.HTTP_OK, "success", videoInfoVos);


    }


    @SaCheckLogin
    @PostMapping("/update_info")
    public ResponseResult<Boolean> updateVideoInfo(@RequestBody VideoInfoUpdateForm form) {

        String loginId = (String) StpUtil.getLoginId();
        Videoinfo videoinfo = videoinfoService.getById(form.getId());
        if (!videoinfo.getAuthorid().equals(loginId)) {
            return new ResponseResult<>(HttpStatus.HTTP_FORBIDDEN, "无操作权限", false);
        }

        LambdaUpdateWrapper<Videoinfo> updateWrapper = new LambdaUpdateWrapper<Videoinfo>().eq(Videoinfo::getId, form.getId())
                .set(Videoinfo::getTitle, form.getTitle())
                .set(Videoinfo::getPartitionId, form.getPartitionId())
                .set(Videoinfo::getSummary, form.getDescription())
                .set(Videoinfo::getIsOriginal, form.isIforiginal() ? 1 : 0);


        boolean update = videoinfoService.update(updateWrapper);

        return update ?
                new ResponseResult<>(HttpStatus.HTTP_OK, "修改成功", true)
                :
                new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR, "修改失败", false);


    }


}
