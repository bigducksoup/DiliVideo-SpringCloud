package com.ducksoup.dilivideovideoencode.mq.provider;


import com.ducksoup.dilivideovideoencode.config.PostRabbitmqConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VideoPostAddProvider {

    private final String ROUTING_KEY = "VIDEO_POST.addVideoPost";

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public Boolean notifyVideoPostAdd(String videoInfoId){
        rabbitTemplate.convertAndSend(PostRabbitmqConfig.VIDEO_POST_EXCHANGE,ROUTING_KEY, videoInfoId);
        return true;
    }

}
