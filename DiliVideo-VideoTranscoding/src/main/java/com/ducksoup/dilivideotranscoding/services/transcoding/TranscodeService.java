package com.ducksoup.dilivideotranscoding.services.transcoding;

import com.ducksoup.dilivideoentity.content.Videofile;

import java.io.File;

public interface TranscodeService {



    void transcodeToMp4(Videofile videoFile) throws Exception;


    File transcodeToMp4(File originFile) throws Exception;

}
