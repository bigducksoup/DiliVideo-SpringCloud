package com.ducksoup.dilivideotranscoding.function;

import com.ducksoup.dilivideotranscoding.entity.VideoFormat;
import com.ducksoup.dilivideotranscoding.entity.VideoQuality;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
@RefreshScope
public class FFmpegTranscoder implements Transcoder {


    @Value("${ffmpeg}")
    private String ffmpegPath;

    public void setFFmpegPath(String ffmpegPath) {
        this.ffmpegPath = ffmpegPath;
    }

    @Override
    public File transcode(File origin, VideoFormat toFormat, VideoQuality quality) throws IOException, InterruptedException {
        String inputFilePath = origin.getAbsolutePath();

        File tempFile = File.createTempFile(inputFilePath.substring(0, inputFilePath.lastIndexOf('.')), "." + toFormat.name().toLowerCase());

        Process process = createTranscodeProcess(quality, tempFile, inputFilePath);

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Failed to transcode video");
        }

        return tempFile;
    }


    private Process createTranscodeProcess(VideoQuality quality, File tempFile, String inputFilePath) throws IOException {
        String tempPath = tempFile.getAbsolutePath();

        String commandTemplate = ffmpegPath + " -y -i %s -vcodec libx264 -acodec aac";
        if (quality != VideoQuality.ORIGINAL) {
            commandTemplate += " -b:v " + quality.getBitRate();
        }
        commandTemplate += " %s";

        String command = String.format(commandTemplate, inputFilePath,tempPath);
        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));


        Process process = processBuilder.start();
        return process;
    }
}