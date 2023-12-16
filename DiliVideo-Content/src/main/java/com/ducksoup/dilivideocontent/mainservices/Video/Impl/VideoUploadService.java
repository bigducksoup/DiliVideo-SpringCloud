package com.ducksoup.dilivideocontent.mainservices.Video.Impl;


import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.ducksoup.dilivideocontent.controller.Params.MissionDoneCallbackParams;
import com.ducksoup.dilivideocontent.controller.Params.UploadChunkParams;
import com.ducksoup.dilivideocontent.controller.Params.UploadMissionParams;
import com.ducksoup.dilivideocontent.entity.VideoChunk;
import com.ducksoup.dilivideocontent.entity.VideoUploadMission;
import com.ducksoup.dilivideocontent.entity.Videoinfo;
import com.ducksoup.dilivideocontent.entity.VideoinfoTag;
import com.ducksoup.dilivideocontent.enums.MISSION_CODE;
import com.ducksoup.dilivideocontent.mainservices.MinIO.MinIOImpl.DownLoadFromMinIOService;
import com.ducksoup.dilivideocontent.mapper.VideoinfoTagMapper;
import com.ducksoup.dilivideocontent.service.VideoChunkService;
import com.ducksoup.dilivideocontent.service.VideoUploadMissionService;
import com.ducksoup.dilivideocontent.utils.OSSUtils;
import com.ducksoup.dilivideocontent.utils.RedisUtil;
import com.ducksoup.dilivideocontent.vo.ChunkUploadInfo;
import com.ducksoup.dilivideoentity.constant.CONSTANT_CONTENT;
import com.ducksoup.dilivideoentity.constant.CONSTANT_MinIO;
import com.ducksoup.dilivideoentity.constant.CONSTANT_STATUS;
import com.ducksoup.dilivideoentity.mq.MQ_FILE_COMBINE;
import com.ducksoup.dilivideoentity.mq.messages.VideoCombineMsg;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@Service
@AllArgsConstructor
public class VideoUploadService {

    private VideoUploadMissionService videoUploadMissionService;

    private DownLoadFromMinIOService minIODownLoadService;

    private RedisUtil redisUtil;

    private final MinioClient minioClient;

    private final VideoChunkService videoChunkService;


    private final OSSUtils ossUtils;

    private final RedissonClient redissonClient;

    private final RabbitTemplate rabbitTemplate;



    private static final int MISSION_NOT_EXIST = 0;

    private static final int HIT_CHUNK_RECORD = 1;

    private static final int CHUNK_MISSING = 2;





    // 创建分片上传任务
    public VideoUploadMission createUploadMission(UploadMissionParams params){

        String loginId = (String) StpUtil.getLoginId();

        VideoUploadMission mission = new VideoUploadMission();
        mission.setId(UUID.fastUUID().toString());
        mission.setFileType(params.getFileType());
        mission.setSize(params.getSize());
        mission.setTotal(params.getTotal());
        mission.setPlatform(params.getPlatform());
        mission.setMd5(params.getMd5());
        mission.setUserId(loginId);
        mission.setStatus(1);
        mission.setCreateTime(DateTime.now());
        mission.setStateCode(MISSION_CODE.CREATED.code);


        if (videoUploadMissionService.save(mission)){
            redisUtil.set(CONSTANT_CONTENT.UPLOAD_MISSION+mission.getId()+loginId,mission,1L, TimeUnit.HOURS);
        }



        return mission;
    }




    /**
     * 如果没有监测到分片存在，则获取上传信息
     * @param params UploadChunkParams
     * @return 上传信息
     */
    public ChunkUploadInfo getChunkUploadInfo(UploadChunkParams params) throws Exception{

        int state = prepareToGetUrl(params);

        switch (state){
            case MISSION_NOT_EXIST: throw new Exception("上传任务不存在");
            case HIT_CHUNK_RECORD: return ChunkUploadInfo.builder()
                    .exists(true)
                    .index(params.getIndex())
                    .md5(params.getMd5()).build();

            case CHUNK_MISSING:{
                try {
                    String url = getChunkUploadUrl(params);
                    return  ChunkUploadInfo.builder().url(url).md5(params.getMd5()).index(params.getIndex()).exists(false).build();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            default:throw new Exception("未知错误");
        }


    }



    /**
     * 如果没有监测到分片存在，则获取上传链接
     * @param params UploadChunkParams
     * @return 上传链接
     */
    public String getChunkUploadUrl(UploadChunkParams params) throws Exception{

        log.info("准备获取分片上传地址,missionId:{},index:{}",params.getMissionId(),params.getIndex());

        String uploadUrl = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .expiry(1, TimeUnit.HOURS)
                    .bucket(CONSTANT_MinIO.VIDEO_CHUNK_BUCKET)
                    .method(Method.PUT)
                    .object(params.getMissionId() + "/" + params.getMd5() + "." + params.getIndex())
                    .build());
        log.info("获取到上传地址,missionId:{},index:{},url:{}",params.getMissionId(),params.getIndex(),uploadUrl);

        return uploadUrl;
    }


    /**
     * 获取上传链接前进行检测，分片是否已经上传
     * @param params UploadChunkParams
     * @return state
     */
    public int prepareToGetUrl(UploadChunkParams params) {

        log.info("检测任务与切片是否已存在,missionId={},index={},md5={}",params.getMissionId(),params.getIndex(),params.getMd5());

        //查询上传任务是否存在
        String loginId = (String) StpUtil.getLoginId();
        String key = CONSTANT_CONTENT.UPLOAD_MISSION + params.getMissionId() + loginId;

        boolean exists = redisUtil.exists(key);

        //任务不存在
        if (!exists){
            log.info("任务不存在,missionId:{}",params.getMissionId());
            return 0;
        }

        //任务存在，切分片存在
        boolean inChunkFile = checkChunkExistsThenCopy(params);
        if (inChunkFile){
            log.info("任务存在，切片存在, missionId:{}",params.getMissionId());
            return 1;
        }

        //任务存在,切分片不存在
        log.info("任务存在,切分片不存在,missionId:{}",params.getMissionId());
        return 2;

    }


    /**
     * 秒传实现
     * @param params UploadChunkParams
     * @return 是否存在并触发秒传
     */
    private boolean checkChunkExistsThenCopy(UploadChunkParams params){


        try {
            //检查是否已经上传
            StatObjectResponse response = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(CONSTANT_MinIO.VIDEO_CHUNK_BUCKET)
                    .object(params.getMissionId() + "/" + params.getMd5() + "." + params.getIndex())
                    .build());

            if (response != null){
                return true;
            }
        }catch (Exception e){
            return false;
        }


        VideoChunk chunk = videoChunkService.checkExist(params.getMd5());
        if (chunk == null){
            return false;
        }


        try {
            //检查历史文件是否存在
            minioClient.copyObject(CopyObjectArgs.builder()
                    .source(CopySource.builder()
                            .bucket(chunk.getChunkBucket())
                            .object(chunk.getChunkPath())
                            .build())
                    .bucket(CONSTANT_MinIO.VIDEO_CHUNK_BUCKET)
                    .object(params.getMissionId()+"/"+params.getMd5()+"."+params.getIndex())
                    .build());
        }catch (Exception e){
            return false;
        }

        return true;
    }


    private final TransactionTemplate transactionTemplate;

    public boolean handleCallBack(MissionDoneCallbackParams params){


        String loginId = (String) StpUtil.getLoginId();

        //上传任务key
        String missionInfoKey = CONSTANT_CONTENT.UPLOAD_MISSION + params.getMissionId() + loginId;


        VideoUploadMission mission = (VideoUploadMission) redisUtil.get(missionInfoKey);

        //检测mission合法性，并且确保幂等
        if (!checkMissionValid(params.getMissionId(),loginId)){
            log.error("任务不符合条件,missionId:{}",params.getMissionId());
            return false;
        }



        //获取上传文件列表
        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(CONSTANT_MinIO.VIDEO_CHUNK_BUCKET)
                .prefix("/" + params.getMissionId())
                .recursive(true)
                .build());


        Iterator<Result<Item>> iterator = results.iterator();

        List<Item> items = new ArrayList<>();

        //获取分片信息
        try {
            while (iterator.hasNext()){
                Result<Item> next = iterator.next();
                Item item = next.get();
                if (item.isDir())continue;
                items.add(item);
            }
        }catch (Exception e){
            log.error(e.getMessage());
            redisUtil.set(missionInfoKey,mission,30L,TimeUnit.MINUTES);
            throw new RuntimeException(e);
        }


        //分片数量不够
        if (items.size()!= mission.getTotal()){
            log.error("分片数量不足,missionId:{}",params.getMissionId());
            redisUtil.set(missionInfoKey,mission,30L,TimeUnit.MINUTES);
            return false;
        }



        //获取分片路径用于后续下载
        List<String> objectNames = items.stream().map(Item::objectName).collect(Collectors.toList());



        //插入videoInfo，并发送消息进行合并与转码
        transactionTemplate.executeWithoutResult(status -> {
            try {

                log.info("事务开启，更新数据库并发送任务，missionId:{}",params.getMissionId());

                Videoinfo videoinfo = insertVideoInfo(params);

                VideoCombineMsg msg = new VideoCombineMsg();
                        msg.setFileType(mission.getFileType());
                        msg.setObjectNames(objectNames);
                        msg.setMissionId(params.getMissionId());
                        msg.setBucket(CONSTANT_MinIO.VIDEO_CHUNK_BUCKET);
                        msg.setLoginId(loginId);
                        msg.setVideoInfoId(videoinfo.getId());


                rabbitTemplate.convertAndSend(MQ_FILE_COMBINE.FILE_COMBINE_EXCHANGE,MQ_FILE_COMBINE.FILE_COMBINE_ROUTTINGKEY,msg);
                //更新任务状态
                videoUploadMissionService.update(new LambdaUpdateWrapper<VideoUploadMission>()
                        .eq(VideoUploadMission::getId,params.getMissionId())
                        .set(VideoUploadMission::getStateCode,MISSION_CODE.HANDLING.code)
                );
            }catch (Exception e){
                log.error("事务失败,missionId:{},error:{}",params.getMissionId(),e.getMessage());
                //失败回滚
                status.setRollbackOnly();
                redisUtil.set(missionInfoKey,mission,30L,TimeUnit.MINUTES);
                throw new RuntimeException(e);
            }
        });

        log.info("消息发送成功，callback结束，missionId:{}",params.getMissionId());
        return true;

    }



    private boolean checkMissionValid(String missionId,String loginId){
        //上传任务key
        String missionInfoKey = CONSTANT_CONTENT.UPLOAD_MISSION + missionId + loginId;

        //判断任务是否存在
        boolean missionExists = redisUtil.exists(missionInfoKey);

        //任务不存在直接返回
        if (!missionExists)return false;


        //任务存在
        //获取分布式锁
        RLock lock = redissonClient.getLock(missionId + loginId);

        //加锁失败直接返回
        if (!lock.tryLock()){
            return false;
        }

        try {
            redisUtil.remove(missionInfoKey);
        }catch (Exception e){
            return false;
        }finally {
            lock.unlock();
        }

        return true;
    }


    private final VideoinfoTagMapper videoinfoTagMapper;

    private Videoinfo insertVideoInfo(MissionDoneCallbackParams params){

        Videoinfo videoinfo = new Videoinfo();
        videoinfo.setId(UUID.fastUUID().toString());
        videoinfo.setTitle(params.getTitle());
        videoinfo.setAuthorName(null);
        videoinfo.setAuthorid(params.getAuthorId());
        videoinfo.setSummary(params.getDescription());
        DateTime now = DateTime.now();
        videoinfo.setCreateTime(now);
        videoinfo.setUpdateTime(now);
        videoinfo.setWatchCount(0L);
        videoinfo.setLikeCount(0L);
        videoinfo.setCollectCount(0);
        videoinfo.setCommentCount(0);
        videoinfo.setMarkStatus(CONSTANT_STATUS.VIDEO_STATUS_HANDLING);
        videoinfo.setStatus(0);
        videoinfo.setIsPublish(1);
        videoinfo.setOpenComment(1);
        videoinfo.setCoverId("");
        videoinfo.setVideofileId("");
        videoinfo.setIsOriginal(params.isIfOriginal()?1:0);
        videoinfo.setPartitionId(params.getPartitionId());

        Db.save(videoinfo);

        if (params.getTagIds()!=null && !params.getTagIds().isEmpty()){
            List<VideoinfoTag> tagMapping = new ArrayList<>();

            for (String tagId : params.getTagIds()) {

                VideoinfoTag videoinfoTag = new VideoinfoTag();
                videoinfoTag.setId(UUID.fastUUID().toString());
                videoinfoTag.setVideoInfoId(videoinfo.getId());
                videoinfoTag.setTagId(tagId);
                videoinfoTag.setTagNo(null);
                videoinfoTag.setCreateTime(now);
                videoinfoTag.setUpdateTime(now);
                videoinfoTag.setStatus(1);

                tagMapping.add(videoinfoTag);
            }

            Db.saveBatch(tagMapping);
            videoinfoTagMapper.updateCodeByVideoInfoId(videoinfo.getId());
        }

        return videoinfo;
    }


}
