package com.ducksoup.dilivideotranscoding.services.ffmpeg.Impl;

import com.ducksoup.dilivideotranscoding.entity.AudioStreamInfo;
import com.ducksoup.dilivideotranscoding.entity.VideoFileDetail;
import com.ducksoup.dilivideotranscoding.entity.VideoStreamInfo;
import com.ducksoup.dilivideotranscoding.services.ffmpeg.FFMpegService;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;
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
    public VideoFileDetail readVideoInfo(File videoFile) throws IOException {
        FFprobe ffprobe = new FFprobe(ffprobePath);

        //读取视频信息
        FFmpegProbeResult probeResult = ffprobe.probe(videoFile.getAbsolutePath());

        //音频流信息
        FFmpegStream audioStream = probeResult.getStreams().get(0);

        //视频流信息
        FFmpegStream videoStream = probeResult.getStreams().get(1);

        VideoStreamInfo videoStreamInfo = new VideoStreamInfo(videoStream);


        AudioStreamInfo audioStreamInfo = new AudioStreamInfo(audioStream);

        VideoFileDetail videoFileDetail = new VideoFileDetail();
        videoFileDetail.setSize(probeResult.getFormat().size);
        videoFileDetail.setDuration(probeResult.getFormat().duration);
        videoFileDetail.setVideoStreamInfo(videoStreamInfo);
        videoFileDetail.setAudioStreamInfo(audioStreamInfo);

        return videoFileDetail;
    }

    @Override
    public File transcode(File originFile, String originFormat, String finalFormat) throws IOException {
        FFmpeg ffmpeg = new FFmpeg(ffmpegPath);
        FFprobe ffprobe = new FFprobe(ffprobePath);

        VideoFileDetail videoFileDetail;


        try {
            videoFileDetail = this.readVideoInfo(originFile);
        }catch (Exception e){
            log.error("视频信息读取失败"+"："+originFile.getName());
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

        File outputTemp = File.createTempFile(originFile.getName(),"."+finalFormat);

        String codec = "libx264";


        FFmpegBuilder fFmpegBuilder = new FFmpegBuilder()
                .addInput(originFile.getAbsolutePath())
                .overrideOutputFiles(true)
                .addOutput(outputTemp.getAbsolutePath())
                .setVideoCodec(codec)
                .disableSubtitle()
                .setAudioChannels(videoFileDetail.getAudioStreamInfo().getChannels())
                .setVideoFrameRate(60,1)
                .setVideoResolution(videoFileDetail.getVideoStreamInfo().getHeight(),videoFileDetail.getVideoStreamInfo().getWidth())
                .setFormat(finalFormat)
                .setAudioCodec(videoFileDetail.getAudioStreamInfo().getCodecName())
                .done();

        FFmpegExecutor fFmpegExecutor = new FFmpegExecutor(ffmpeg,ffprobe);
        log.info(originFile.getName()+"开始转码");


        fFmpegExecutor.createJob(fFmpegBuilder, progress -> log.info(progress.toString())).run();

        originFile.delete();

        return outputTemp;

    }
}
