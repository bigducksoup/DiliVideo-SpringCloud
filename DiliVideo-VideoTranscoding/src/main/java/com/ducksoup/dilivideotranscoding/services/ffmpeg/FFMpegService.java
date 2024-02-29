package com.ducksoup.dilivideotranscoding.services.ffmpeg;

import com.ducksoup.dilivideotranscoding.entity.VideoFileInfo;

import java.io.File;
import java.io.IOException;

public interface FFMpegService {


    VideoFileInfo readVideoInfo(File videoFile) throws IOException;


    File transcode(File originFile,String originFormat,String finalFormat) throws IOException;


    File getFirstFrame(File videoFile) throws IOException, InterruptedException;

}
