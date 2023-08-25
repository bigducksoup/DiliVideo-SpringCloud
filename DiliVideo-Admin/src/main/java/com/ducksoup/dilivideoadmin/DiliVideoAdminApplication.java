package com.ducksoup.dilivideoadmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.ducksoup.dilivideofeign")
@SpringBootApplication
public class DiliVideoAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiliVideoAdminApplication.class, args);
    }

}
