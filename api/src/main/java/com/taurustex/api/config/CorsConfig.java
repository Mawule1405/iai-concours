package com.taurustex.api.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Slf4j
@Configuration
public class CorsConfig {


    @Bean
    public CorsConfigurationSource corsConfigurationSource(@Value("${app.frontend.url}") String frontUrl) {
        CorsConfiguration config = new CorsConfiguration();

        // On autorise l'URL spécifique de ton Angular (ex: http://192.168.1.10)
        config.setAllowedOrigins(List.of(frontUrl));

        // Méthodes autorisées pour les opérations CRUD de Taurus Study Controller
        config.setAllowedMethods(List.of("GET", "POST", "PATCH", "PUT", "DELETE"));

        // Autoriser tous les headers (ou spécifier : "Content-Type", "Authorization", "X-Requested-With")
        config.setAllowedHeaders(List.of("*"));

        // Indispensable pour envoyer/recevoir des Cookies ou l'en-tête Authorization
        config.setAllowCredentials(true);

        // Headers que le client Angular est autorisé à lire
        config.setExposedHeaders(List.of("Set-Cookie", "Authorization"));

        // Durée de mise en cache de la réponse de "pre-flight" (1 heure)
        // Cela évite que le navigateur envoie une requête OPTIONS avant chaque appel API
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}