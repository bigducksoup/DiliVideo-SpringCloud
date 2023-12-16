package com.ducksoup.dilivideotranscoding.mq.consumer;

import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.ducksoup.dilivideoentity.constant.CONSTANT_MinIO;
import com.ducksoup.dilivideoentity.constant.CONSTANT_STATUS;
import com.ducksoup.dilivideoentity.dto.FileHandleResult;
import com.ducksoup.dilivideoentity.dto.TranscodeFileInfo;
import com.ducksoup.dilivideoentity.mq.MQ_FILE_COMBINE;
import com.ducksoup.dilivideoentity.mq.messages.VideoCombineMsg;
import com.ducksoup.dilivideofeign.content.VideoInfoServices;
import com.ducksoup.dilivideotranscoding.services.ffmpeg.FFMpegService;
import com.ducksoup.dilivideotranscoding.services.filecombine.FileCombineService;
import com.ducksoup.dilivideotranscoding.services.filestorage.FileDownloadService;
import com.ducksoup.dilivideotranscoding.services.filestorage.Impl.MinioFileUploadService;
import com.ducksoup.dilivideotranscoding.services.transcoding.TranscodeService;
import com.ducksoup.dilivideotranscoding.utils.OSSUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@AllArgsConstructor
public class FileCombineConsumer {


    private final FileDownloadService fileDownloadService;

    private final FileCombineService fileCombineService;

    private final OSSUtils ossUtils;

    private final MinioFileUploadService minioFileUploadService;

    private final TranscodeService transcodeService;

    private final FFMpegService ffMpegService;

    private final ThreadPoolTaskExecutor IOThreadPool;


    /**
     * 合并文件并转换为mp4格式
     *
     * @param payload VideoCombineMsg
     * @param message Message
     * @throws Exception 异常
     *                   TODO test
     */
    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(MQ_FILE_COMBINE.FILE_COMBINE_QUEUE),
                    exchange = @Exchange(MQ_FILE_COMBINE.FILE_COMBINE_EXCHANGE),
                    key = {MQ_FILE_COMBINE.FILE_COMBINE_ROUTTINGKEY})})
    public void combineVideoChunk(VideoCombineMsg payload, Message message) throws Exception {

        handleMessage(payload,message);

    }


    private void handleMessage(VideoCombineMsg payload, Message message) throws Exception {
        log.info("payload:【{}】,message:【{}】", payload.toString(), message.getMessageProperties());


        log.info("开始下载分片文件,missionId:{}", payload.getMissionId());
        //下载分片文件
        List<File> chunkFiles = fileDownloadService.multiThreadsDownLoad(payload.getObjectNames(), payload.getBucket());

        log.info("开始排序文件,missionId:{}", payload.getMissionId());
        //根据文件名称排序，防止合并文件后，因顺序错乱，导致不可用
        List<File> sortFiles = sortFiles(chunkFiles);


        log.info("开始合并文件,missionId:{}", payload.getMissionId());
        // combine files
        File result = fileCombineService.combine(sortFiles, payload.getFileType());

        String fileMd5 = DigestUtil.md5Hex(result);
        //  7d/97/26fd5eac-1214-4d7d-b383-215c1c566824181358662907874205.mp4
        String path = ossUtils.generatePath(fileMd5, result.getName());

        String[] dir = path.split("/");
        //  7d/97/
        String pathPrefix = dir[0] + "/" + dir[1] + "/";


        File mp4File = null;
        //回调更新数据库的消息
        FileHandleResult resultMessage = new FileHandleResult();
        resultMessage.setMissionId(payload.getMissionId());
        resultMessage.setVideoInfoId(payload.getVideoInfoId());

        try {
            // 26fd5eac-1214-4d7d-b383-215c1c566824181358662907874205.mp41191456016177053723.mp4
            mp4File = transcodeService.transcodeToMp4(result);
        } catch (Exception e) {

            log.error("转码失败，missionId:{},error:{}", payload.getMissionId(), e.getMessage());
            //设置为文件错误
            resultMessage.setFileStatus(CONSTANT_STATUS.VIDEO_STATUS_FILE_ERROR);

            // TODO notify failed
            notify(resultMessage);
            return;
        }

        String mp4Path = pathPrefix + mp4File.getName();

        // TODO getCover
        log.info("开始获取视频封面,missionId:{}", payload.getMissionId());
        File cover = ffMpegService.getFirstFrame(mp4File);
        String coverPath = payload.getMissionId() + "/" + cover.getName();

        log.info("begin upload file:【{}】,bucket: {}", path, CONSTANT_MinIO.VIDEO_BUCKET);
        //上传源文件以及处理好的文件
        String md5Hex = DigestUtil.md5Hex(mp4File);

        long fs = FileUtil.size(mp4File);

        try {
            minioFileUploadService.upload(result, CONSTANT_MinIO.VIDEO_BUCKET, path);
            minioFileUploadService.upload(mp4File, CONSTANT_MinIO.VIDEO_BUCKET, mp4Path);
            minioFileUploadService.upload(cover, CONSTANT_MinIO.COVER_BUCKET, coverPath);
        } catch (Exception e) {
            log.error("upload file error:{},file : {},chunk :{}", e.getMessage(), path, CONSTANT_MinIO.VIDEO_BUCKET);
            throw new RuntimeException(e);
        } finally {
            result.delete();
            mp4File.delete();
            cover.delete();
        }
        log.info("上传视频文件以及封面正常,missionId:{}", payload.getMissionId());


        // missionId
        // videoInfoId
        // mission status
        // origin file path
        // origin file bucket
        // md5
        // size
        // List of transcode file info
        //  ---bucket
        //  ---path
        //  ---file type
        //  ---md5
        //  ---w
        //  ---h
        //  ---quality
        //  ---size
        // chunks paths
        // chunks bucket

        resultMessage.setSize(FileUtil.size(result));
        resultMessage.setChunkBucket(payload.getBucket());
        resultMessage.setChunkPaths(payload.getObjectNames());
        resultMessage.setOriginFileBucket(CONSTANT_MinIO.VIDEO_BUCKET);
        resultMessage.setOriginFilePath(path);
        resultMessage.setMd5(fileMd5);
        resultMessage.setFileStatus(CONSTANT_STATUS.VIDEO_STATUS_CHECKING);

        resultMessage.setCoverBucket(CONSTANT_MinIO.COVER_BUCKET);
        resultMessage.setCoverPath(coverPath);

        TranscodeFileInfo transcodeFileInfo = new TranscodeFileInfo();
        transcodeFileInfo.setBucket(CONSTANT_MinIO.VIDEO_BUCKET);
        transcodeFileInfo.setPath(mp4Path);
        transcodeFileInfo.setFileType("mp4");
        transcodeFileInfo.setMd5(md5Hex);
        transcodeFileInfo.setW(0);
        transcodeFileInfo.setH(0);
        transcodeFileInfo.setQuality("1080p");
        transcodeFileInfo.setSize(fs);

        resultMessage.setTranscodeFileInfos(new ArrayList<>());
        resultMessage.getTranscodeFileInfos().add(transcodeFileInfo);


        //TODO notify content service to update video info
        log.info("通知content服务,missionId:{}", payload.getMissionId());
        notify(resultMessage);
    }


    //根据后缀排序，防止合并后因顺序不一致导致文件损坏
    private List<File> sortFiles(List<File> originFiles) {

        List<File> sortedFiles = originFiles.stream().sorted(
                Comparator.comparing(
                        File::getName,
                        Comparator.comparing(
                                f -> Integer.valueOf(f.split("\\.")[1])
                        ))).collect(Collectors.toList());


        return sortedFiles;
    }


    private final VideoInfoServices videoInfoServices;

    /**
     * 回调content更新数据库信息
     *
     * @param resultMessage
     */
    private void notify(FileHandleResult resultMessage) {
        videoInfoServices.onTranscodingDone(resultMessage);
    }


}
