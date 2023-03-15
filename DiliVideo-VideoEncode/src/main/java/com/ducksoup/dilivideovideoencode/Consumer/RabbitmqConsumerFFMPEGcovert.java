package com.ducksoup.dilivideovideoencode.Consumer;

import com.ducksoup.dilivideoentity.ContentEntity.Videofile;
import com.ducksoup.dilivideovideoencode.Config.RabbitmqConfig;
import com.ducksoup.dilivideovideoencode.Services.DownLoadFromMinIOService;
import com.ducksoup.dilivideovideoencode.Services.FFMPEGService;
import com.ducksoup.dilivideovideoencode.Services.UpLoadToMinIOService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Slf4j
public class RabbitmqConsumerFFMPEGcovert {



    @Autowired
    private DownLoadFromMinIOService downLoadFromMinIOService;

    @Autowired
    private UpLoadToMinIOService upLoadToMinIOService;

    @Autowired
    private FFMPEGService ffmpegService;

    @RabbitListener(queues = RabbitmqConfig.FFMPEG_QUEUE)
    public void Consumer1(Videofile payload, Message message){
        log.info("payload:【{}】,message:【{}】",payload.toString(),message.getMessageProperties());

        try {
            //下载
            File origin = downLoadFromMinIOService.downLoadOnject("video", payload.getFullpath(), payload.getPath(), payload.getUniqueName());
            //转码
            File file = ffmpegService.ffmpegEncode(origin, payload);
            //上传
            boolean b = upLoadToMinIOService.upLoadFile(file, payload);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

    }
}