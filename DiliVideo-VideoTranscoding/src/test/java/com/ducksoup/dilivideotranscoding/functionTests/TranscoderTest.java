package com.ducksoup.dilivideotranscoding.functionTests;

import com.ducksoup.dilivideotranscoding.entity.VideoFormat;
import com.ducksoup.dilivideotranscoding.entity.VideoQuality;
import com.ducksoup.dilivideotranscoding.function.FFmpegTranscoder;
import org.junit.jupiter.api.Test;

import java.io.File;

public class TranscoderTest {



    @Test
    public void TestFFmpegTranscoder(){

        FFmpegTranscoder fFmpegTranscoder = new FFmpegTranscoder();
        fFmpegTranscoder.setFFmpegPath("/usr/local/bin/ffmpeg");
        File originFile = new File("/Users/meichuankutou/Downloads/视频素材/1.mp4");

        try {

            File transcode = fFmpegTranscoder.transcode(originFile, VideoFormat.FLV, VideoQuality.HIGH);
            System.out.println(transcode.getAbsolutePath());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
