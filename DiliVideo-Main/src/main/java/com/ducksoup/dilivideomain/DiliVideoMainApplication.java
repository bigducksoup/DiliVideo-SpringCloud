package com.ducksoup.dilivideomain;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.ducksoup.dilivideofeign.BarrageServices")
@MapperScan("com.ducksoup.dilivideomain.mapper")
public class DiliVideoMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiliVideoMainApplication.class, args);
    }

}
