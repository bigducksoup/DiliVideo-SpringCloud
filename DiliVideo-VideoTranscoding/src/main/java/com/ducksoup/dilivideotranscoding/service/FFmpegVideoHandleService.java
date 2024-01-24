package com.ducksoup.dilivideotranscoding.service;

import com.ducksoup.dilivideotranscoding.entity.VideoFormat;
import com.ducksoup.dilivideotranscoding.entity.VideoQuality;
import com.ducksoup.dilivideotranscoding.function.Transcoder;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.File;


@Service
@AllArgsConstructor
public class FFmpegVideoHandleService implements VideoHandleService, InitializingBean {

    private Transcoder fFmpegTranscoder;

    @Override
    public File transcode(File origin, VideoFormat toFormat, VideoQuality quality) throws Exception {
        return fFmpegTranscoder.transcode(origin, toFormat, quality);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ServiceContext.videoHandleServiceMap.put("FFmpegVideoHandleService",this);
    }
}
