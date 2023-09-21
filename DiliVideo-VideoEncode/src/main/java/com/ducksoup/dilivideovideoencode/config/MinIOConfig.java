package com.ducksoup.dilivideovideoencode.config;



import com.ducksoup.dilivideovideoencode.config.reader.MinIOConfReader;
import io.minio.MinioClient;

import lombok.Data;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 传统连接MinIO方式
 * @author 明快de玄米61
 * @date   2022/12/15 14:22
 **/
@Data
@Configuration
@EnableConfigurationProperties(MinIOConfReader.class)
public class MinIOConfig {

    @Resource
    private MinIOConfReader minIOConfReader;

    /**
     * 注入minio 客户端
     */
    @Bean
    public MinioClient minioClient() {

        return MinioClient.builder()
                .endpoint(minIOConfReader.getEndpoint())
                .credentials(minIOConfReader.getUserName(), minIOConfReader.getPassword())
                .build();
    }


}
