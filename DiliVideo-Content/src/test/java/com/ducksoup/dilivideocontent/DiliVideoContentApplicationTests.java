package com.ducksoup.dilivideocontent;

import com.ducksoup.dilivideocontent.config.RabbitmqConfig;
import com.ducksoup.dilivideocontent.entity.Videofile;
import com.ducksoup.dilivideocontent.mainservices.MinIO.UploadService;
import com.ducksoup.dilivideocontent.mainservices.UserOperation.LikeOperationService;
import com.ducksoup.dilivideocontent.mainservices.Video.Impl.VideoUploadService;
import com.ducksoup.dilivideocontent.service.VideofileService;
import com.ducksoup.dilivideoentity.constant.CONSTANT_MinIO;
import com.ducksoup.dilivideoentity.mq.MQ_FILE_COMBINE;
import com.ducksoup.dilivideoentity.mq.messages.VideoCombineMsg;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PostPolicy;
import io.minio.Result;
import io.minio.messages.Item;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


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

    @Resource
    private VideoUploadService videoUploadService;

    @Autowired
    private MinioClient minioClient;

    @Test
    void TestRabbit(){
        Videofile byId = videofileService.getById("746437c3-88dc-4feb-9e58-ecd66fc7db30");
        rabbitTemplate.convertAndSend(RabbitmqConfig.FFMPEG_EXCHANGE, ROUTING_KEY1, byId);
        System.out.println("success");
    }



    @Test
    void TestLike(){
        likeOperationService.updateLikeCount("8269d8d5-a0c3-42de-b942-bc44844b2fe0");
    }



}
