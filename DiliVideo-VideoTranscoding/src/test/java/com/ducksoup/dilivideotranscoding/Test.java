package com.ducksoup.dilivideotranscoding;

import com.ducksoup.dilivideotranscoding.entity.VideoFileInfo;
import com.ducksoup.dilivideotranscoding.function.FFmpegVideoInfoReader;
import com.ducksoup.dilivideotranscoding.function.VideoInfoReader;

import java.io.File;

public class Test {



    @org.junit.jupiter.api.Test
    public void TestVideoInfoReader() throws Exception {

        String ffmprobePath = "/Users/meichuankutou/Public/ffmp/ffprobe";

        String testVideoPath = "/Users/meichuankutou/Public/ffmp/test.mp4";

        VideoInfoReader reader = new FFmpegVideoInfoReader(ffmprobePath);

        VideoFileInfo info = reader.read(new File(testVideoPath));

        System.out.println(info.toString());


    }

}
