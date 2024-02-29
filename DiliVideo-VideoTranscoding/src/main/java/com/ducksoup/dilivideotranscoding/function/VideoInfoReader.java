package com.ducksoup.dilivideotranscoding.function;

import com.ducksoup.dilivideotranscoding.entity.AudioStreamInfo;
import com.ducksoup.dilivideotranscoding.entity.VideoFileInfo;
import com.ducksoup.dilivideotranscoding.entity.VideoStreamInfo;

import java.io.File;
import java.io.IOException;

public interface VideoInfoReader {


    VideoFileInfo read(File file) throws Exception;

    VideoStreamInfo readVideoStreamInfo(File file) throws Exception;

    AudioStreamInfo readAudioStreamInfo(File file) throws Exception;

}
