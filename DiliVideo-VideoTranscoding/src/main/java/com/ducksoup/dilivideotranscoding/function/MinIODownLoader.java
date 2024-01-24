package com.ducksoup.dilivideotranscoding.function;

import cn.hutool.core.io.IoUtil;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.*;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;


/**
 * MinIO的DownLoader实现
 * @see DownLoader
 */
@AllArgsConstructor
@Builder
public class MinIODownLoader implements DownLoader{

    private String bucket;

    private String region;

    private String object;

    private Map<String,String> extraHeaders;

    private Map<String,String> extraQueryParams;

    private MinioClient minioClient;

    @Override
    public File download() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {


        boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                        .bucket(bucket)
                        .region(region)
                        .extraHeaders(extraHeaders)
                        .extraQueryParams(extraQueryParams)
                        .build());

        if (!bucketExists){
            throw new RuntimeException("bucket not exist");
        }


        GetObjectResponse response = minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucket)
                .region(region)
                .object(object)
                .extraHeaders(extraHeaders)
                .extraQueryParams(extraQueryParams)
                .build());

        String[] split = object.split("\\.");

        String prefix = split[0];

        String suffix = "." + split[1];

        File file = File.createTempFile(prefix,suffix);

        IoUtil.copy(response, Files.newOutputStream(file.toPath()));

        return file;
    }
}
