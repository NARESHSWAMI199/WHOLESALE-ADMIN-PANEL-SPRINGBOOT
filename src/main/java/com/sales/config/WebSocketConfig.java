package com.sales.config;

import com.sales.interceptors.SalesHandshakeInterceptor;
import com.sales.wholesaler.services.WholesaleUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    WholesaleUserService wholesaleUserService;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic","/user","/queue");
        config.setApplicationDestinationPrefixes("/app");
//        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat")
//                .withSockJS()
                .setAllowedOrigins("*")
                .addInterceptors(new SalesHandshakeInterceptor(wholesaleUserService));
    }

}
