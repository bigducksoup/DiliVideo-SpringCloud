package com.ducksoup.dilivideocontent;

import com.ducksoup.dilivideocontent.Config.RabbitmqConfig;
import com.ducksoup.dilivideocontent.Entity.Videofile;
import com.ducksoup.dilivideocontent.mainservices.MinIO.UploadService;
import com.ducksoup.dilivideocontent.mainservices.UserOperation.LikeOperationService;
import com.ducksoup.dilivideocontent.service.VideofileService;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;



@SpringBootTest
class DiliVideoContentApplicationTests {


    public final static String ROUTING_KEY1 = "FFMPEG.convert";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private VideofileService videofileService;

    @Autowired
    private LikeOperationService likeOperationService;

    @Test
    void TestRabbit(){
        Videofile byId = videofileService.getById("746437c3-88dc-4feb-9e58-ecd66fc7db30");
        rabbitTemplate.convertAndSend(RabbitmqConfig.FFMPEG_EXCHANGE, ROUTING_KEY1, byId);
        System.out.println("success");
    }



    @Test
    void TestLike(){
        boolean b = likeOperationService.updateLikeCount("8269d8d5-a0c3-42de-b942-bc44844b2fe0");
    }




}
