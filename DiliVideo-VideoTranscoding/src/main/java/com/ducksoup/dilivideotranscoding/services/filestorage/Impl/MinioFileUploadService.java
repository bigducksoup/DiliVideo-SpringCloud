package com.ducksoup.dilivideotranscoding.services.filestorage.Impl;

import cn.hutool.core.io.file.FileNameUtil;
import com.ducksoup.dilivideotranscoding.services.filestorage.FileUploadService;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;


@Service
public class MinioFileUploadService implements FileUploadService {


    @Resource
    private MinioClient minioClient;

    @Override
    public boolean upload(File file, String bucket, String path) throws Exception {


        String name = FileNameUtil.getName(path);

        String extName = FileNameUtil.extName(file);

        String newPathName = name+"."+extName;


        minioClient.uploadObject(UploadObjectArgs.builder()
                        .bucket(bucket)
                        .object(path)
                        .filename(file.getAbsolutePath())
                .build());

        file.delete();
        return true;
    }

}
