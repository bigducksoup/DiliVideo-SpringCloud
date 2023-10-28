package com.ducksoup.dilivideotranscoding.mq.consumer;

import com.ducksoup.dilivideoentity.constant.CONSTANT_MinIO;
import com.ducksoup.dilivideoentity.content.FileInfoUpdateParam;
import com.ducksoup.dilivideoentity.content.Videofile;
import com.ducksoup.dilivideofeign.content.ContentServices;
import com.ducksoup.dilivideotranscoding.config.FFMPEGRabbitmqConfig;
import com.ducksoup.dilivideotranscoding.mq.provider.VideoPostAddProvider;
import com.ducksoup.dilivideotranscoding.services.DownLoadFromMinIOService;
import com.ducksoup.dilivideotranscoding.services.UpLoadToMinIOService;
import com.ducksoup.dilivideotranscoding.services.ffmpeg.FFMpegService;
import com.ducksoup.dilivideotranscoding.services.filestorage.FileDownloadService;
import com.ducksoup.dilivideotranscoding.services.filestorage.FileUploadService;
import com.ducksoup.dilivideotranscoding.services.filestorage.Impl.MinioFileDownloadService;
import com.ducksoup.dilivideotranscoding.services.transcoding.TranscodeService;
import com.ducksoup.dilivideotranscoding.utils.OSSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

@Component
@Slf4j
public class RabbitMQConsumerFFMPEGConvert {








    @Autowired
    private OSSUtils ossUtils;


    @Resource
    private TranscodeService transcodeService;

    @Autowired
    private VideoPostAddProvider videoPostAddProvider;

    @Autowired
    private ContentServices contentServices;



    @RabbitListener(queues = FFMPEGRabbitmqConfig.FFMPEG_QUEUE)
    public void Consumer1(Videofile payload, Message message){
        log.info("payload:【{}】,message:【{}】",payload.toString(),message.getMessageProperties());

        try {

            String url = ossUtils.makeUrl(payload.getBucket(), payload.getPath());
            //下载

            //转码
            transcodeService.transcodeToMp4(payload);

            //上传

            contentServices.setVideoInfoStatus(new FileInfoUpdateParam(payload.getVideoinfoId(), payload.getId(), 1));
            videoPostAddProvider.notifyVideoPostAdd(payload.getVideoinfoId());


        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

    }
}