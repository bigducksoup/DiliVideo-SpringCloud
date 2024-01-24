package com.ducksoup.dilivideotranscoding.function;

import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.UploadObjectArgs;
import io.minio.errors.*;
import lombok.Builder;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;


@Builder
public class MinIOUpLoader implements UpLoader{

    private MinioClient minioClient;

    private String bucket;

    private String region;

    private String object;

    private String contentType;

    private Map<String,String> extraHeaders;

    private Map<String,String> extraQueryParams;

    private Map<String,String> headers;




    @Override
    public void upload(File file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        ObjectWriteResponse response = minioClient.uploadObject(UploadObjectArgs.builder()
                .bucket(bucket)
                .object(object)
                .headers(headers)
                .extraHeaders(extraHeaders)
                .extraQueryParams(extraQueryParams)
                .filename(file.getAbsolutePath())
                .build());
    }
}
