package com.ducksoup.dilivideocontent;

import com.ducksoup.dilivideofeign.BarrageServices.TestServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.ducksoup.dilivideofeign.BarrageServices")
@EnableDiscoveryClient
public class DiliVideoContentApplication {



    public static void main(String[] args) {
        SpringApplication.run(DiliVideoContentApplication.class, args);


    }

}
