package com.ducksoup.dilivideotranscoding.service;

import com.ducksoup.dilivideotranscoding.entity.VideoFormat;
import com.ducksoup.dilivideotranscoding.entity.VideoQuality;

import java.io.File;

public interface VideoHandleService {


    File transcode(File origin, VideoFormat toFormat, VideoQuality quality) throws Exception;

}
