package com.ducksoup.dilivideovideoencode.Services;


import com.ducksoup.dilivideoentity.ContentEntity.Videofile;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFmpegUtils;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class FFMPEGService {

    @Value("${ffmpeg}")
    private String ffmpegPath;

    @Value("${ffprobe}")
    private String ffprobePath;


    public File ffmpegEncode(File origin, Videofile fileInfo) throws IOException {
        FFmpeg ffmpeg = new FFmpeg(ffmpegPath);
        FFprobe ffprobe = new FFprobe(ffprobePath);

        //设置临时文件
        File outputTemp = File.createTempFile("temp",".mp4");

        FFmpegProbeResult in = ffprobe.probe(origin.getAbsolutePath());

        FFmpegBuilder fFmpegBuilder = new FFmpegBuilder()
                .addInput(origin.getAbsolutePath())
                .overrideOutputFiles(true)
                .addOutput(outputTemp.getAbsolutePath())
                .setVideoCodec("libx264")
                .disableSubtitle()
                .setAudioChannels(1)
                .setVideoFrameRate(60,1)
                .setVideoResolution(1920,1080)
                .setFormat("mp4")
                .setAudioCodec("aac")
                .done();
        FFmpegExecutor fFmpegExecutor = new FFmpegExecutor(ffmpeg,ffprobe);
        log.info(fileInfo.getFullpath()+"开始转码");
        fFmpegExecutor.createJob(fFmpegBuilder,
                new ProgressListener() {
                    final double duration_ns = in.getFormat().duration * TimeUnit.SECONDS.toNanos(1);

                    @Override
                    public void progress(Progress progress) {
                        System.out.println(progress);
                        System.out.println(origin.length());
                    }
                }
        ).run();
        boolean delete = origin.delete();
        return outputTemp;

    }



}
