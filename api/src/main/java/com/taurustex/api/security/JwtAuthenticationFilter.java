package com.taurustex.api.security;

import com.taurustex.api.models.AppUser;
import com.taurustex.api.repositories.AppUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AppUserRepository appUserRepository; // Ajouté
    private final HandlerExceptionResolver exceptionResolver; // Pour uniformiser les erreurs
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Authentication attemptAuthentication(@NonNull HttpServletRequest request,
                                                @NonNull HttpServletResponse response) throws AuthenticationException {
        try {
            // Lecture du body JSON (username/password)
            UserRequest userRequest = objectMapper.readValue(request.getInputStream(), UserRequest.class);
            log.info("Tentative de connexion : {}", userRequest.getUsername());

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userRequest.getUsername(), userRequest.getPassword())
            );
        } catch (IOException e) {
            log.error("Erreur de désérialisation du login : {}", e.getMessage());
            throw new RuntimeException("Format de requête invalide", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException {

        User user = (User) authResult.getPrincipal();

        // Récupération de l'utilisateur pour vérifier son état et ses infos étendues
        AppUser appUser = appUserRepository.findByUsernameOrEmail(user.getUsername(), user.getUsername())
                .orElseThrow(() -> new RuntimeException("Données utilisateur introuvables"));

        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        // Génération des tokens (Refresh + Access)
        TokenResponse tokens = jwtUtil.generateTokens(user, roles, request);

        // 1. Création du Cookie sécurisé pour le Refresh Token
        ResponseCookie resCookie = ResponseCookie.from("refresh", tokens.getRefresh())
                .httpOnly(true)
                .secure(true) // Obligatoire quand sameSite est à "None"
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax") // Autorise l'envoi du cookie entre sous-domaines Cross-Site
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, resCookie.toString());

        // 2. Réponse JSON avec l'Access Token (Angular le stockera en mémoire vive)
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> body = new HashMap<>();
        body.put("access", tokens.getAccess());
        body.put("username", appUser.getUsername());
        body.put("fullName", appUser.getFirstName() + " " + appUser.getLastName());

        objectMapper.writeValue(response.getOutputStream(), body);
        log.info("Connexion réussie pour : {}", user.getUsername());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) {
        log.warn("Échec d'authentification : {}", failed.getMessage());

        // Délégation au GlobalExceptionHandler pour avoir un ApiError propre
        exceptionResolver.resolveException(request, response, null, failed);
    }
}