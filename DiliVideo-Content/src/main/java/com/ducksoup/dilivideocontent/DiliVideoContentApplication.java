package com.ducksoup.dilivideocontent;

import com.ducksoup.dilivideoentity.Result.ErrorCode;
import com.ducksoup.dilivideoentity.Result.ResultUtils;
import com.ducksoup.dilivideofeign.BarrageServices.TestServices;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.ducksoup.dilivideofeign.BarrageServices")
@MapperScan("com.ducksoup.dilivideocontent.mapper")
public class DiliVideoContentApplication {


    public static void main(String[] args) {
        SpringApplication.run(DiliVideoContentApplication.class, args);
    }

}
