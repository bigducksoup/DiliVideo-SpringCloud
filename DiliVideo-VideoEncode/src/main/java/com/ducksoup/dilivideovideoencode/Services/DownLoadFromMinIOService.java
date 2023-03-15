package com.ducksoup.dilivideovideoencode.Services;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;

@Service
public class DownLoadFromMinIOService {

    @Autowired
    private MinioClient minioClient;


    public File downLoadOnject(String bucket,String fullPath,String path,String filename) throws Exception{

        bucket = "video";

        InputStream response = minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucket)
                .object(path)
                .build());

        String[] split = filename.split("\\.");

        File temp = File.createTempFile("temp","."+split[split.length-1]);
//        File f = new File("/Users/meichuankutou/Desktop/testsss.MOV");
        FileUtils.copyInputStreamToFile(response,temp);

        return temp;

    }



}
