package com.ducksoup.dilivideovideoencode;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class DiliVideoVideoEncodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiliVideoVideoEncodeApplication.class, args);
    }

}
