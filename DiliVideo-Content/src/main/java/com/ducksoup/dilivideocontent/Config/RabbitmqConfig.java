package com.ducksoup.dilivideocontent.Config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableRabbit
public class RabbitmqConfig {
    public final static String FFMPEG_EXCHANGE = "FFMPEG_EXCHANGE";
    public final static String FFMPEG_QUEUE = "ffmpeg_queue";
    public final static String FFMPEG_ROUTINGKEY= "FFMPEG.*";


    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean(FFMPEG_EXCHANGE)
    public Exchange exchange() {
        return ExchangeBuilder
                .topicExchange(FFMPEG_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean(FFMPEG_QUEUE)
    public Queue queue() {
        return new Queue(FFMPEG_QUEUE);
    }

    @Bean
    public Binding binding(
            @Qualifier(FFMPEG_EXCHANGE) Exchange exchange
            , @Qualifier(FFMPEG_QUEUE) Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(FFMPEG_ROUTINGKEY)
                .noargs();
    }
}