package com.ducksoup.dilivideovideoencode.services;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;

/**
 * 从minIO下载文件的实现
 */
@Service
public class DownLoadFromMinIOService {

    @Autowired
    private MinioClient minioClient;


    public File downLoadObject(String bucket,String fullPath,String path,String filename) throws Exception{


        InputStream response = minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucket)
                .object(path)
                .build());

        String[] split = filename.split("\\.");

        File temp = File.createTempFile("temp","."+split[split.length-1]);

        FileUtils.copyInputStreamToFile(response,temp);

        response.close();

        return temp;

    }



}
