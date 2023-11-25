package com.ducksoup.dilivideotext.mq.consumer;

import com.ducksoup.dilivideotext.config.PostRabbitmqConfig;
import com.ducksoup.dilivideotext.mainservices.PostOperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class VideoPostAddConsumer {


    @Autowired
    private PostOperationService postOperationService;

    /**
     *
     * @param videoInfoId 视频信息Id
     * @param message 消息
     * @throws Exception 保存module失败
     */
    @RabbitListener(queues = PostRabbitmqConfig.VIDEO_POST_QUEUE)
    public void VideoPostAdd(String videoInfoId, Message message) throws Exception {
        log.info("videoInfoId:【{}】,message:【{}】", videoInfoId, message.getMessageProperties());
        postOperationService.saveVideoPublishPost(videoInfoId);
    }

}
