package com.taurustex.api.security;


import com.taurustex.api.repositories.AppUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.time.LocalDateTime;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class WebSecurity {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final AppUserRepository userRepository;
    private final HandlerExceptionResolver exceptionResolver;

    // Constructeur manuel pour forcer le Qualifier
    public WebSecurity(
            AuthenticationConfiguration authenticationConfiguration,
            JwtUtil jwtUtil,
            AppUserRepository userRepository,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver
    ) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.exceptionResolver = exceptionResolver;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        log.info("[{}] Initialisation de la chaîne de sécurité", LocalDateTime.now());

        AuthenticationManager authManager = authenticationManager();

        // 1. Filtre Login : On ajoute l'exceptionResolver pour gérer les échecs de login proprement
        JwtAuthenticationFilter authFilter = new JwtAuthenticationFilter(
                authManager,
                jwtUtil,
                userRepository,
                exceptionResolver
        );
        authFilter.setFilterProcessesUrl("/login");

        // 2. Filtre JWT : Vérification des accès à chaque requête
        JwtAuthorizationFilter authorizationFilter = new JwtAuthorizationFilter(
                userRepository,
                jwtUtil,
                exceptionResolver
        );

        http
                .csrf(AbstractHttpConfigurer::disable)
                // Utilise la configuration CORS définie dans ton bean corsConfigurationSource
                .cors(Customizer.withDefaults())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)) // Pour la console H2 si besoin

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login",
                                "/iai-concours-api/auth/refresh",
                                "/iai-concours-api/public/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/ws-iai-concours-api/**", // Handshake WebSocket
                                "/error"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // Positionnement des filtres
                .addFilter(authFilter)
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class)

                // Centralisation des erreurs 401 et 403 vers le GlobalExceptionHandler
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) ->
                                exceptionResolver.resolveException(request, response, null, authException))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                exceptionResolver.resolveException(request, response, null, accessDeniedException))
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}