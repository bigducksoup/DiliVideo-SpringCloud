package com.ducksoup.dilivideotranscoding.services.transcoding.Impl;

import cn.hutool.core.io.file.FileNameUtil;
import com.ducksoup.dilivideoentity.constant.CONSTANT_MinIO;
import com.ducksoup.dilivideoentity.content.Videofile;
import com.ducksoup.dilivideotranscoding.services.ffmpeg.FFMpegService;
import com.ducksoup.dilivideotranscoding.services.filestorage.FileDownloadService;
import com.ducksoup.dilivideotranscoding.services.filestorage.FileUploadService;
import com.ducksoup.dilivideotranscoding.services.transcoding.TranscodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;


@Component
public class DefaultTranscodeService implements TranscodeService {

    @Autowired
    private FFMpegService ffMpegService;

    @Autowired
    private FileDownloadService fileDownloadService;

    @Autowired
    private FileUploadService uploadService;



    @Override
    public void transcodeToMp4(Videofile videoFile) throws Exception {

        File origin = fileDownloadService.download(videoFile.getUniqueName(), videoFile.getPath(), CONSTANT_MinIO.VIDEO_BUCTET);

        String extName = FileNameUtil.extName(origin);

        File mp4 = ffMpegService.transcode(origin, extName, "mp4");

        boolean b = uploadService.upload(mp4, CONSTANT_MinIO.VIDEO_BUCTET, videoFile.getPath());


    }
}
