package com.taurustex.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${app.frontend.url}")
    private String frontUrl;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Destinations pour les messages sortants (Server -> Client)
        config.enableSimpleBroker("/topic");

        // Destination pour les messages entrants (Client -> Server)
        config.setApplicationDestinationPrefixes("/app");

        // Préfixe pour les messages ciblés sur un utilisateur spécifique
        // Cela permet d'utiliser SimpMessagingTemplate.convertAndSendToUser()
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-iai-concours-api")
                .setAllowedOrigins(frontUrl) // Utilise ton URL Angular définie dans properties
                .withSockJS();
    }
}