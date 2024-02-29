package com.ducksoup.dilivideotranscoding.function;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.common.utils.JacksonUtils;
import com.ducksoup.dilivideotranscoding.entity.AudioStreamInfo;
import com.ducksoup.dilivideotranscoding.entity.VideoFileInfo;
import com.ducksoup.dilivideotranscoding.entity.VideoStreamInfo;
import com.ducksoup.dilivideotranscoding.entity.ffmpeg.FFmpegVideoInfo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
@Component
@AllArgsConstructor
@NoArgsConstructor
public class FFmpegVideoInfoReader  implements VideoInfoReader{



    @Value("${ffprobe}")
    private String ffprobePath;

    private final String template = "%s -v quiet -print_format json -show_format -show_streams %s";


    @Override
    public VideoFileInfo read(File file) throws IOException, InterruptedException {

        //format command for reading video info
        String cmd = String.format(template, ffprobePath,file.getAbsolutePath());
        ProcessBuilder processBuilder = new ProcessBuilder(cmd.split(" "));

        // run command
        Process process = processBuilder.start();

        // block and wait for run result
        int exitCode = process.waitFor();
        byte[] data;

        // error occur
        if (exitCode != 0) {
            data = new byte[process.getErrorStream().available()];
            int read = process.getErrorStream().read(data);
            throw new RuntimeException(new String(data));
        }

        // read result and convert to result type
        data = new byte[process.getInputStream().available()];
        int read = process.getInputStream().read(data);
        String jsonStr = new String(data);


        JSON json = JSONUtil.parse(jsonStr);

        FFmpegVideoInfo fFmpegVideoInfo = json.toBean(FFmpegVideoInfo.class);

        return makeVideoFileInfo(fFmpegVideoInfo);
    }


    private static VideoFileInfo makeVideoFileInfo(FFmpegVideoInfo fFmpegVideoInfo) {
        VideoStreamInfo videoStreamInfo = new VideoStreamInfo(fFmpegVideoInfo.getStreams().get(0));
        AudioStreamInfo audioStreamInfo = new AudioStreamInfo(fFmpegVideoInfo.getStreams().get(1));

        VideoFileInfo info = new VideoFileInfo();
        info.setSize(Long.parseLong(fFmpegVideoInfo.getFormat().getSize()));
        info.setDuration(Double.parseDouble(fFmpegVideoInfo.getFormat().getDuration()));
        info.setVideoStreamInfo(videoStreamInfo);
        info.setAudioStreamInfo(audioStreamInfo);
        return info;
    }

    @Override
    public VideoStreamInfo readVideoStreamInfo(File file) throws IOException, InterruptedException {
        VideoFileInfo read = read(file);
        return  read.getVideoStreamInfo();
    }

    @Override
    public AudioStreamInfo readAudioStreamInfo(File file) throws IOException, InterruptedException {
        VideoFileInfo read = read(file);
        return read.getAudioStreamInfo();
    }
}
