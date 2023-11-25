package com.ducksoup.dilivideocontent.mainservices.MinIO.MinIOImpl;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.messages.Item;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * 从minIO下载文件的实现
 */
@Service
public class DownLoadFromMinIOService {

    @Autowired
    private MinioClient minioClient;


    public File downLoadObject(String bucket,String path,String filename) throws Exception{


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
