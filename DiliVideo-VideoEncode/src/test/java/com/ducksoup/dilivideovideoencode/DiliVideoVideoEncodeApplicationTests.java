package com.ducksoup.dilivideovideoencode;

import com.ducksoup.dilivideovideoencode.Services.FFMPEGService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;

@SpringBootTest
class DiliVideoVideoEncodeApplicationTests {


    @Autowired
    private FFMPEGService ffmpegService;
    @Test
    void contextLoads() throws IOException, InterruptedException {


    }

}
