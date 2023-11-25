package com.ducksoup.dilivideotext.config;


import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PostRabbitmqConfig {
    public final static String VIDEO_POST_EXCHANGE = "VIDEO_POST_EXCHANGE";
    public final static String VIDEO_POST_QUEUE = "VIDEO_POST_QUEUE";
    public final static String VIDEO_POST_ROUTINGKEY= "VIDEO_POST.*";




    @Bean(VIDEO_POST_EXCHANGE)
    public Exchange VIDEOPOST_exchange() {
        return ExchangeBuilder
                .topicExchange(VIDEO_POST_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean(VIDEO_POST_QUEUE)
    public Queue VIDEOPOST_queue() {
        return new Queue(VIDEO_POST_QUEUE);
    }

    @Bean
    public Binding VIDEOPOST_binding(
            @Qualifier(VIDEO_POST_EXCHANGE) Exchange exchange
            , @Qualifier(VIDEO_POST_QUEUE) Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(VIDEO_POST_ROUTINGKEY)
                .noargs();
    }
}