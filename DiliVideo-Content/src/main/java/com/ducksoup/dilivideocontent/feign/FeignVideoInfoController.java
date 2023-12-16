package com.ducksoup.dilivideocontent.feign;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.UUID;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.ducksoup.dilivideocontent.entity.Cover;
import com.ducksoup.dilivideocontent.entity.Videofile;
import com.ducksoup.dilivideocontent.entity.Videoinfo;
import com.ducksoup.dilivideocontent.mainservices.MinIO.MinIOImpl.MetaDataService;
import com.ducksoup.dilivideocontent.service.VideofileService;
import com.ducksoup.dilivideocontent.service.VideoinfoService;
import com.ducksoup.dilivideocontent.utils.OSSUtils;
import com.ducksoup.dilivideoentity.constant.CONSTANT_MinIO;
import com.ducksoup.dilivideoentity.constant.CONSTANT_STATUS;
import com.ducksoup.dilivideoentity.content.FileInfoUpdateParam;
import com.ducksoup.dilivideoentity.content.VideoAuthorInfoUpdateParams;
import com.ducksoup.dilivideoentity.content.VideoInfoUpdateParams;
import com.ducksoup.dilivideoentity.dto.FileHandleResult;
import com.ducksoup.dilivideoentity.dto.TranscodeFileInfo;
import com.ducksoup.dilivideoentity.result.ResponseResult;

import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/feign/video_info")
public class FeignVideoInfoController {

    @Autowired
    private VideoinfoService videoinfoService;

    @Autowired
    private VideofileService videofileService;

    @Autowired
    private OSSUtils ossUtils;


    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private MetaDataService metaDataService;

    @GetMapping("/getById")
    public ResponseResult<Videoinfo> getVideoInfoById(@RequestParam String videoInfoId){

        Videoinfo videoinfo = videoinfoService.getById(videoInfoId);

        return new ResponseResult<>(HttpStatus.HTTP_OK,"success",videoinfo);
    }

    @PostMapping("/update_file_info")
    public boolean setStatus(@RequestBody FileInfoUpdateParam fileStatusUpdateParam) {

        Videofile videofile = videofileService.getById(fileStatusUpdateParam.getVideoFileId());


        String path = videofile.getPath();
        String uniqueName = videofile.getUniqueName();


        String newPath = path.split("\\.")[0]+".mp4";
        String newUniqueName = uniqueName.split("\\.")[0]+".mp4";
        String newFullPath = ossUtils.makeUrl(videofile.getBucket(),newPath);

        videofile.setPath(newPath);
        videofile.setUniqueName(newUniqueName);
        videofile.setFullpath(newFullPath);


        videofileService.updateById(videofile);
        videoinfoService.update(new LambdaUpdateWrapper<Videoinfo>().eq(Videoinfo::getId,videofile.getVideoinfoId()).set(Videoinfo::getMarkStatus, CONSTANT_STATUS.VIDEO_STATUS_CHECKING));


        return videoinfoService.update(
                new LambdaUpdateWrapper<Videoinfo>()
                        .eq(Videoinfo::getId,fileStatusUpdateParam.getVideoInfoId())
                        .set(Videoinfo::getStatus,fileStatusUpdateParam.getStatus()));

    }



    @PostMapping("/update_video_info")
    public Boolean updateVideoInfo(@RequestBody VideoInfoUpdateParams params){

        Videoinfo videoinfo = new Videoinfo();
        BeanUtil.copyProperties(params,videoinfo);

        return videoinfoService.updateById(videoinfo);
    }


    @PostMapping("/update_video_author_info")
    public Boolean updateVideoAuthorInfo(@RequestBody VideoAuthorInfoUpdateParams params){

        return videoinfoService.update(new LambdaUpdateWrapper<Videoinfo>().eq(Videoinfo::getAuthorid,params.getAuthorId())
                .set(params.getNickName()!=null,Videoinfo::getAuthorName,params.getNickName())
                .set(params.getSummary()!=null,Videoinfo::getSummary,params.getSummary())
        );

    }




    //TODO on transcoding done
    @PostMapping("/transcoding_done")
    public void onTranscodingDone(@RequestBody FileHandleResult fileHandleResult){

        Videoinfo videoinfo = Db.getById(fileHandleResult.getVideoInfoId(), Videoinfo.class);


        //处理文件错误
        if (fileHandleResult.getFileStatus().equals(CONSTANT_STATUS.VIDEO_STATUS_FILE_ERROR)){
            //更新视频信息状态为错误
            Db.update(Wrappers.lambdaUpdate(Videoinfo.class).eq(Videoinfo::getId,fileHandleResult.getVideoInfoId()).set(Videoinfo::getMarkStatus,CONSTANT_STATUS.VIDEO_STATUS_FILE_ERROR));
            return;
        }

        List<Videofile> files = new ArrayList<>();

        //原始视频信息
        Videofile originFile = new Videofile();
        originFile.setId(UUID.fastUUID().toString());
        originFile.setOriginName(fileHandleResult.getOriginFilePath());
        originFile.setUniqueName(fileHandleResult.getOriginFilePath());
        originFile.setPath(fileHandleResult.getOriginFilePath());
        originFile.setBucket(fileHandleResult.getOriginFileBucket());
        originFile.setUploadTime(DateTime.now());
        originFile.setVideoinfoId(fileHandleResult.getVideoInfoId());
        originFile.setSize(fileHandleResult.getSize());
        originFile.setFullpath("null");
        originFile.setMd5(fileHandleResult.getMd5());

        files.add(originFile);


        //转码后的视频
        for (TranscodeFileInfo fileInfo : fileHandleResult.getTranscodeFileInfos()) {

            Videofile transcodeFile = new Videofile();
            transcodeFile.setId(UUID.fastUUID().toString());
            transcodeFile.setOriginName(fileInfo.getPath());
            transcodeFile.setUniqueName(fileInfo.getPath());
            transcodeFile.setPath(fileInfo.getPath());
            transcodeFile.setBucket(fileInfo.getBucket());
            transcodeFile.setUploadTime(DateTime.now());
            transcodeFile.setVideoinfoId(fileHandleResult.getVideoInfoId());
            transcodeFile.setSize(fileInfo.getSize());
            transcodeFile.setFullpath("null");
            transcodeFile.setMd5(fileInfo.getMd5());
            files.add(transcodeFile);
        }


        //判断用户是否以及上传了文件
        Cover cover = new Cover();
        cover.setId(UUID.fastUUID().toString());cover.setUploadTime(DateTime.now());
        String dir = "/" + fileHandleResult.getMissionId() + "/custom";
        List<Item> items = metaDataService.ListItemsInDir(CONSTANT_MinIO.COVER_BUCKET,dir, true);
        if (items.isEmpty()){
            //封面文件
            cover.setOriginalName(fileHandleResult.getCoverPath());
            cover.setUniqueName(fileHandleResult.getCoverPath());
            cover.setPath(fileHandleResult.getCoverPath());
            cover.setBucket(fileHandleResult.getCoverBucket());
        }else {
            Item item = items.get(0);
            String path = item.objectName();
            cover.setOriginalName(path);
            cover.setUniqueName(path);
            cover.setPath(path);
        }
        cover.setFullpath("null");
        cover.setMd5("null");

        transactionTemplate.executeWithoutResult(status -> {
            //存储
            boolean r1 = Db.save(cover);
            boolean r2 = Db.saveBatch(files);
            boolean r3 = Db.update(Wrappers.lambdaUpdate(Videoinfo.class).eq(Videoinfo::getId, fileHandleResult.getVideoInfoId()).set(Videoinfo::getMarkStatus, CONSTANT_STATUS.VIDEO_STATUS_CHECKING)
                    .set(Videoinfo::getCoverId, cover.getId()).set(Videoinfo::getVideofileId,files.get(0).getId()));

            if (!(r1 && r2 && r3)){
                status.setRollbackOnly();
            }

        });

    }



}
