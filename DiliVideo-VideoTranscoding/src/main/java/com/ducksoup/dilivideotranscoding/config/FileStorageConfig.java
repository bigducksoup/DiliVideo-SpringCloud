package com.ducksoup.dilivideotranscoding.config;


import com.ducksoup.dilivideotranscoding.services.filestorage.FileDownloadService;
import com.ducksoup.dilivideotranscoding.services.filestorage.FileInfoService;
import com.ducksoup.dilivideotranscoding.services.filestorage.FileUploadService;
import com.ducksoup.dilivideotranscoding.services.filestorage.Impl.MinioFileDownloadService;
import com.ducksoup.dilivideotranscoding.services.filestorage.Impl.MinioFileInfoService;
import com.ducksoup.dilivideotranscoding.services.filestorage.Impl.MinioFileUploadService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileStorageConfig {




    @Bean
    @ConditionalOnMissingBean
    FileDownloadService fileDownloadService(){
        return new MinioFileDownloadService();
    }


    @Bean
    @ConditionalOnMissingBean
    FileUploadService fileUploadService(){
        return new MinioFileUploadService();
    }

    @Bean
    @ConditionalOnMissingBean
    FileInfoService fileInfoService(){
        return new MinioFileInfoService();
    }

}
