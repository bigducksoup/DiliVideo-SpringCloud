package com.ducksoup.dilivideolive.config;


import com.ducksoup.dilivideolive.websocket.handler.LiveMessageHandler;
import com.ducksoup.dilivideolive.websocket.interceptor.WebsocketHandShakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;



@Configuration
public class WebSocketConfig implements WebSocketConfigurer {


    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }


    @Autowired
    private WebsocketHandShakeInterceptor websocketHandShakeInterceptor;

    @Autowired
    private WebSocketHandler liveMessageHandler;


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(liveMessageHandler,"/msg/{id}")
                .setAllowedOrigins("*")
                .addInterceptors(websocketHandShakeInterceptor);
    }
}
