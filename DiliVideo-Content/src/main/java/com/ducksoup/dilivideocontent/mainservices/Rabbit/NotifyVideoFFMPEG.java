package com.ducksoup.dilivideocontent.mainservices.Rabbit;

import com.ducksoup.dilivideocontent.Config.RabbitmqConfig;
import com.ducksoup.dilivideocontent.Entity.Videofile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class NotifyVideoFFMPEG {

    public final static String ROUTING_KEY1 = "FFMPEG.convert";
    @Autowired
    private RabbitTemplate rabbitTemplate;


    public Boolean notifyVideoFFMPG(Videofile videofile){
        rabbitTemplate.convertAndSend(RabbitmqConfig.FFMPEG_EXCHANGE,ROUTING_KEY1,videofile);
        log.info(videofile.getId()+"发送至队列进行转码");
        return true;
    }

}
