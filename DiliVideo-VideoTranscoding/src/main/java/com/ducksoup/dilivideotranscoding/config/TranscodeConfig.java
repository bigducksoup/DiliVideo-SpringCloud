package com.ducksoup.dilivideotranscoding.config;


import com.ducksoup.dilivideotranscoding.services.transcoding.Impl.DefaultTranscodeService;
import com.ducksoup.dilivideotranscoding.services.transcoding.TranscodeService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TranscodeConfig {


    @Bean
    @ConditionalOnMissingBean
    TranscodeService transcodeService(){
        return new DefaultTranscodeService();
    }

}
