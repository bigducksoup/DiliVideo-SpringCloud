package com.ducksoup.dilivideotranscoding.services.filestorage.Impl;

import com.ducksoup.dilivideotranscoding.services.filestorage.FileInfoService;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;


@Slf4j
public class MinioFileInfoService implements FileInfoService {

    @Resource
    private MinioClient minioClient;

    @Override
    public boolean checkFileExist(String fileName, String path, String bucket) {

        try {
            StatObjectResponse response = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucket)
                    .object(path)
                    .build());

            return response!=null;
        }catch (Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

    }
}
