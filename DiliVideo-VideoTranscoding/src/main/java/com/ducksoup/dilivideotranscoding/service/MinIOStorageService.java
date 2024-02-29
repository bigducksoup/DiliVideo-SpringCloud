package com.ducksoup.dilivideotranscoding.service;

import com.ducksoup.dilivideotranscoding.function.DownLoader;
import com.ducksoup.dilivideotranscoding.function.UpLoader;
import io.minio.errors.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class MinIOStorageService implements StorageService, InitializingBean {
    @Override
    public void upload(UpLoader upLoader,File file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        upLoader.upload(file);
    }

    @Override
    public File download(DownLoader downLoader) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return downLoader.download();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ServiceContext.storageServiceMap.put("MinIOStorageService",this);
    }
}
