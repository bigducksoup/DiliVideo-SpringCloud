package com.ducksoup.dilivideovideoencode.Services;


import com.ducksoup.dilivideoentity.contentEntity.Videofile;
import com.ducksoup.dilivideovideoencode.entity.AudioStreamInfo;
import com.ducksoup.dilivideovideoencode.entity.VideoFileDetail;
import com.ducksoup.dilivideovideoencode.entity.VideoStreamInfo;
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
import java.util.concurrent.TimeUnit;

/**
 * 转码服务
 */
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


        VideoFileDetail fileOriginInfo = this.getVideoFileOriginInfo(origin.getAbsoluteFile());

        //设置临时文件
        File outputTemp = File.createTempFile("temp",".mp4");


        //读取文件信息
        FFmpegProbeResult in = ffprobe.probe(origin.getAbsolutePath());

        String codec = "libx264";
        String format = "mp4";
        FFmpegBuilder fFmpegBuilder = new FFmpegBuilder()
                .addInput(origin.getAbsolutePath())
                .overrideOutputFiles(true)
                .addOutput(outputTemp.getAbsolutePath())
                .setVideoCodec(codec)
                .disableSubtitle()
                .setAudioChannels(fileOriginInfo.getAudioStreamInfo().getChannels())
                .setVideoFrameRate(60,1)
                .setVideoResolution(fileOriginInfo.getVideoStreamInfo().getHeight(),fileOriginInfo.getVideoStreamInfo().getWidth())
                .setFormat(format)
                .setAudioCodec(fileOriginInfo.getVideoStreamInfo().getCodec_name())
                .done();
        FFmpegExecutor fFmpegExecutor = new FFmpegExecutor(ffmpeg,ffprobe);
        log.info(fileInfo.getFullpath()+"开始转码");
        fFmpegExecutor.createJob(fFmpegBuilder,
                new ProgressListener() {
                    final double duration_ns = in.getFormat().duration * TimeUnit.SECONDS.toNanos(1);

                    @Override
                    public void progress(Progress progress) {
                        log.info(progress.toString());
                        log.info(String.valueOf(origin.length()));
                    }
                }
        ).run();
        boolean delete = origin.delete();
        return outputTemp;

    }






    public VideoFileDetail getVideoFileOriginInfo(File originFile) throws IOException {

        FFprobe ffprobe = new FFprobe(ffprobePath);

        //读取视频信息
        FFmpegProbeResult probeResult = ffprobe.probe(originFile.getAbsolutePath());

        //视频流信息
        FFmpegStream videoStream = probeResult.getStreams().get(0);

        //音频流信息
        FFmpegStream audioStream = probeResult.getStreams().get(1);

        VideoStreamInfo videoStreamInfo = new VideoStreamInfo(videoStream);


        AudioStreamInfo audioStreamInfo = new AudioStreamInfo(audioStream);

        VideoFileDetail videoFileDetail = new VideoFileDetail();
        videoFileDetail.setSize(probeResult.getFormat().size);
        videoFileDetail.setDuration(probeResult.getFormat().duration);
        videoFileDetail.setVideoStreamInfo(videoStreamInfo);
        videoFileDetail.setAudioStreamInfo(audioStreamInfo);

        return videoFileDetail;

    }



}
