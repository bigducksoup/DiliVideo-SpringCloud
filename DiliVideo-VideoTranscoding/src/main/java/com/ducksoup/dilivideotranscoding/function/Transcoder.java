package com.ducksoup.dilivideotranscoding.function;

import com.ducksoup.dilivideotranscoding.entity.VideoFormat;
import com.ducksoup.dilivideotranscoding.entity.VideoQuality;

import java.io.File;

public interface Transcoder {
    File transcode(File origin, VideoFormat toFormat, VideoQuality quality) throws Exception;
}
