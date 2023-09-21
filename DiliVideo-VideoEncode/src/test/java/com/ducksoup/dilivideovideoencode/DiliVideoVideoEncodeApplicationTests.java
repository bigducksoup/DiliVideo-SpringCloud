package com.ducksoup.dilivideovideoencode;

import com.ducksoup.dilivideovideoencode.mq.provider.VideoPostAddProvider;
import com.ducksoup.dilivideovideoencode.services.FFMPEGService;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class DiliVideoVideoEncodeApplicationTests {


    @Autowired
    private FFMPEGService ffmpegService;


    @Autowired
    private VideoPostAddProvider postAddProvider;

    @Test
    void contextLoads() throws Exception {
        postAddProvider.notifyVideoPostAdd("71c99045-dcb7-43f8-90e2-a6b8c42d401f");


    }

}
