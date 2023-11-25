package com.ducksoup.dilivideocontent.config;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.redisson.config.TransportMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {



    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private String port;
    @Value("${spring.redis.password}")
    private String password;


    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.setTransportMode(TransportMode.NIO);
        // 单体配置，如果是SSL连接，使用 rediss://
        SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setAddress("redis://" + host + ":" + port);
        singleServerConfig.setPassword(password);
        return Redisson.create(config);
    }




}
