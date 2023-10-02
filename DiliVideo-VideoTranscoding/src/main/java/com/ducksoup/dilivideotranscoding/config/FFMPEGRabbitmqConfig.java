package com.ducksoup.dilivideotranscoding.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class FFMPEGRabbitmqConfig {
    public final static String FFMPEG_EXCHANGE = "FFMPEG_EXCHANGE";
    public final static String FFMPEG_QUEUE = "ffmpeg_queue";
    public final static String FFMPEG_ROUTINGKEY= "FFMPEG.*";




    @Bean(FFMPEG_EXCHANGE)
    public Exchange FFMPEG_exchange() {
        return ExchangeBuilder
                .topicExchange(FFMPEG_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean(FFMPEG_QUEUE)
    public Queue FFMPEG_queue() {
        return new Queue(FFMPEG_QUEUE);
    }

    @Bean
    public Binding FFMPEG_binding(
            @Qualifier(FFMPEG_EXCHANGE) Exchange exchange
            , @Qualifier(FFMPEG_QUEUE) Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(FFMPEG_ROUTINGKEY)
                .noargs();
    }
}