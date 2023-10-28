package com.ducksoup.dilivideotranscoding.services.filestorage.Impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import com.ducksoup.dilivideotranscoding.services.filestorage.FileDownloadService;
import com.ducksoup.dilivideotranscoding.services.filestorage.FileInfoService;
import io.minio.DownloadObjectArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;


@Slf4j
public class MinioFileDownloadService implements FileDownloadService {

    @Autowired
    private MinioClient minioClient;


    @Autowired
    private FileInfoService minioFileInfoService;

    @Override
    public File download(String fileName, String path, String bucket) throws Exception {

        boolean fileExist = minioFileInfoService.checkFileExist(fileName, path, bucket);

        if (!fileExist){
            log.error("{} not exist,bucket:{},path:{}",fileName,bucket,path);
            throw new RuntimeException("file not found");
        }


        GetObjectArgs downloadObjectArgs = GetObjectArgs.builder()
                .bucket(bucket)
                .object(path)
                .build();

        GetObjectResponse inputStream = minioClient.getObject(downloadObjectArgs);

        String extName = "." + FileNameUtil.extName(fileName);
        File tempFile = File.createTempFile(fileName, extName);

        FileUtil.writeFromStream(inputStream,tempFile);

        return tempFile;
    }


}
