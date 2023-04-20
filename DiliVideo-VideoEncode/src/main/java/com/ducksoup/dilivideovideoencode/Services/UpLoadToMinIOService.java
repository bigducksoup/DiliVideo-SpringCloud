package com.ducksoup.dilivideovideoencode.Services;


import com.ducksoup.dilivideoentity.ContentEntity.Videofile;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.UploadObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;


/**
 * 上传服务
 */
@Service
public class UpLoadToMinIOService {

    @Autowired
    private MinioClient minioClient;


    public boolean upLoadFile(File file, Videofile fileInfo) throws Exception {

        String ori = fileInfo.getPath();

        String[] split = ori.split("\\.");

        String prefix = split[0];

        String obj = prefix+".mp4";



        minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket("video")
                        .object(obj)
                        .filename(file.getAbsolutePath())
                        .build()
        );

        boolean delete = file.delete();

        return true;
    }

}
