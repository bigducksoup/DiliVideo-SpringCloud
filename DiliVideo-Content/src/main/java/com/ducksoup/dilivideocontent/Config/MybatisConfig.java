package com.ducksoup.dilivideocontent.Config;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.ducksoup.dilivideocontent.mapper")
public class MybatisConfig {
}
