package com.ducksoup.dilivideotranscoding;

import com.ducksoup.dilivideotranscoding.function.MinIODownLoader;
import com.ducksoup.dilivideotranscoding.mq.provider.VideoPostAddProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
class DiliVideoVideoEncodeApplicationTests {


    @Autowired
    private VideoPostAddProvider postAddProvider;




    @Test
    void contextLoads() throws Exception {
        MinIODownLoader build = MinIODownLoader.builder().build();

        File download = build.download();
    }

}
