package com.ducksoup.dilivideotranscoding.services.ffmpeg;

import com.ducksoup.dilivideotranscoding.entity.VideoFileDetail;

import java.io.File;
import java.io.IOException;

public interface FFMpegService {


    VideoFileDetail readVideoInfo(File videoFile) throws IOException;


    File transcode(File originFile,String originFormat,String finalFormat) throws IOException;



}