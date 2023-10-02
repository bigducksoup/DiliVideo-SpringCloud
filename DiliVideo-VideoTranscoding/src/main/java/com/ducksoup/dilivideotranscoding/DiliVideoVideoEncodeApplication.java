package com.ducksoup.dilivideotranscoding;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableRabbit
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.ducksoup.dilivideofeign")
public class DiliVideoVideoEncodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiliVideoVideoEncodeApplication.class, args);
    }

}
