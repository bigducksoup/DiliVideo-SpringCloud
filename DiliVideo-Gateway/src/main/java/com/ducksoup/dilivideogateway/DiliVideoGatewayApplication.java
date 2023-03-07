package com.ducksoup.dilivideogateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DiliVideoGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiliVideoGatewayApplication.class, args);
    }

}
