package com.ducksoup.dilivideotranscoding.services.ffmpeg.Impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import com.ducksoup.dilivideotranscoding.entity.AudioStreamInfo;
import com.ducksoup.dilivideotranscoding.entity.VideoFileInfo;
import com.ducksoup.dilivideotranscoding.entity.VideoStreamInfo;
import com.ducksoup.dilivideotranscoding.services.ffmpeg.FFMpegService;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;


@Slf4j
@Service
public class FFMpegServiceImpl implements FFMpegService {

    @Value("${ffmpeg}")
    private String ffmpegPath;

    @Value("${ffprobe}")
    private String ffprobePath;



    @Override
    public VideoFileInfo readVideoInfo(File videoFile) throws IOException {
        FFprobe ffprobe = new FFprobe(ffprobePath);

        //读取视频信息
        FFmpegProbeResult probeResult = ffprobe.probe(videoFile.getAbsolutePath());

        //音频流信息
        FFmpegStream videoStream = probeResult.getStreams().get(0);

        //视频流信息
        FFmpegStream audioStream = probeResult.getStreams().get(1);

        VideoStreamInfo videoStreamInfo = new VideoStreamInfo(videoStream);
        AudioStreamInfo audioStreamInfo = new AudioStreamInfo(audioStream);

        VideoFileInfo videoFileInfo = new VideoFileInfo();
        videoFileInfo.setSize(probeResult.getFormat().size);
        videoFileInfo.setDuration(probeResult.getFormat().duration);
        videoFileInfo.setVideoStreamInfo(videoStreamInfo);
        videoFileInfo.setAudioStreamInfo(audioStreamInfo);

        return videoFileInfo;
    }

    @Override
    public File transcode(File originFile, String originFormat, String finalFormat) throws IOException {
        FFmpeg ffmpeg = new FFmpeg(ffmpegPath);
        FFprobe ffprobe = new FFprobe(ffprobePath);

        VideoFileInfo videoFileInfo;


        try {
            videoFileInfo = this.readVideoInfo(originFile);
        }catch (Exception e){
            log.error("视频信息读取失败"+"："+originFile.getName());
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

        File outputTemp = File.createTempFile(originFile.getName().split("\\.")[0],"."+finalFormat);

        String codec = "libx264";


        FFmpegBuilder fFmpegBuilder = new FFmpegBuilder()
                .addInput(originFile.getAbsolutePath())
                .overrideOutputFiles(true)
                .addOutput(outputTemp.getAbsolutePath())
                .setVideoCodec(codec)
                .disableSubtitle()
                .setAudioChannels(videoFileInfo.getAudioStreamInfo().getChannels())
                .setVideoFrameRate(60,1)
                .setVideoResolution(videoFileInfo.getVideoStreamInfo().getHeight(), videoFileInfo.getVideoStreamInfo().getWidth())
                .setFormat(finalFormat)
                .setAudioCodec(videoFileInfo.getAudioStreamInfo().getCodecName())
                .done();

        FFmpegExecutor fFmpegExecutor = new FFmpegExecutor(ffmpeg,ffprobe);
        log.info(originFile.getName()+"开始转码");


        fFmpegExecutor.createJob(fFmpegBuilder, progress -> log.info(progress.toString())).run();

        return outputTemp;

    }


    //TODO test
   @Override
    public File getFirstFrame(File videoFile) throws IOException, InterruptedException {

        log.info("获取封面，file={}",videoFile.getAbsolutePath());

        //Create a temporary file with the file name "frame" and the file extension ".png"
        File tempFile = File.createTempFile(UUID.fastUUID().toString(), ".png");

       String absolutePath = tempFile.getAbsolutePath();

       tempFile.delete();

       //Create a process builder to run the ffmpeg command
        ProcessBuilder command = new ProcessBuilder().command(ffmpegPath, "-i", videoFile.getAbsolutePath(), "-vf", "select=eq(n\\,0)", "-vframes", "1", absolutePath);

        //Run the command and wait for it to finish
        command.start().waitFor();

       File file = FileUtil.file(absolutePath);
       //Return the temporary file
        return file;
    }


}
