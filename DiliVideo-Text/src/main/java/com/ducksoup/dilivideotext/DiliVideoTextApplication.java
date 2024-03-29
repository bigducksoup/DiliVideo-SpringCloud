package com.ducksoup.dilivideotext;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableRabbit
@EnableCaching
@EnableTransactionManagement
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.ducksoup.dilivideofeign")
@MapperScan("com.ducksoup.dilivideotext.mapper")
public class DiliVideoTextApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiliVideoTextApplication.class, args);
    }

}
