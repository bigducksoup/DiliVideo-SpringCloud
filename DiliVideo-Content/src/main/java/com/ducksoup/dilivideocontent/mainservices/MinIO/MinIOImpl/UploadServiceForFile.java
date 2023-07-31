package com.ducksoup.dilivideocontent.mainservices.MinIO.MinIOImpl;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import com.ducksoup.dilivideocontent.dto.FileSavedInfo;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class UploadServiceForFile {


    @Autowired
    private MinioClient minioClient;

    public FileSavedInfo uploadFile(File file, String BucketName, String md5) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {



        //后缀
        String suffix = FileUtil.getSuffix(file);
        //id
        String uuid = UUID.randomUUID().toString();
        //文件名
        String fileName = uuid+"."+suffix;
        //文件夹（相对于bucket）
        String folder = md5.substring(0,2)+"/"+md5.substring(2,4)+"/";
        //相对于bucket的路径
        String obj = folder+fileName;

        minioClient.putObject(PutObjectArgs.builder()
                .bucket(BucketName)
                .stream(Files.newInputStream(file.toPath()), FileUtil.size(file), -1)
                .object(obj)
                .build());

        FileSavedInfo fileSavedInfo = new FileSavedInfo();
        fileSavedInfo.setFileName(fileName);
        fileSavedInfo.setSuffix(suffix);
        fileSavedInfo.setPath(obj);
        return fileSavedInfo;
    }

}
