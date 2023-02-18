package com.ducksoup.dilivideobarrage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DiliVideoBarrageApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiliVideoBarrageApplication.class, args);
    }

}
