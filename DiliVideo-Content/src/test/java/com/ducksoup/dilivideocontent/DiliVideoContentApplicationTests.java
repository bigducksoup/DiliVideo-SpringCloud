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



    @Test
    void testListObject(){


        String missionId = "cb51df09-7f03-46dc-90e4-38db84d69ab0";


        //获取上传文件列表
        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(CONSTANT_MinIO.VIDEO_CHUNK_BUCKET)
                .prefix("/" + missionId)
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
            throw new RuntimeException(e);
        }





        //获取分片路径用于后续下载
        List<String> objectNames = items.stream().map(Item::objectName).collect(Collectors.toList());


        VideoCombineMsg msg = new VideoCombineMsg();

        msg.setVideoInfoId("123");
        msg.setMissionId(missionId);
        msg.setLoginId("2");
        msg.setObjectNames(objectNames);
        msg.setBucket(CONSTANT_MinIO.VIDEO_CHUNK_BUCKET);
        msg.setFileType("mp4");


        rabbitTemplate.convertAndSend(MQ_FILE_COMBINE.FILE_COMBINE_EXCHANGE,MQ_FILE_COMBINE.FILE_COMBINE_ROUTTINGKEY,msg);


        System.out.println(123);
    }


}
