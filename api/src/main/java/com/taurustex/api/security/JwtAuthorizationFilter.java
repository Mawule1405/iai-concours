package com.taurustex.api.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.taurustex.api.models.AppUser;
import com.taurustex.api.repositories.AppUserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final AppUserRepository userRepository;
    private final JwtUtil jwtUtil;
    // Injection du resolver d'exceptions de Spring
    private final org.springframework.web.servlet.HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {


        if(JwtUtil.isPermittedPath(request.getServletPath())){
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader(JwtUtil.AUTH_HEADER);

        if (authorizationHeader == null || !authorizationHeader.startsWith(JwtUtil.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authorizationHeader.substring(JwtUtil.TOKEN_PREFIX.length());
            Algorithm algorithm = Algorithm.HMAC512(jwtUtil.getSecret());
            JWTVerifier jwtVerifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = jwtVerifier.verify(token);

            String username = decodedJWT.getSubject();
            List<String> roles = decodedJWT.getClaim("roles").asList(String.class);

            AppUser appUser = userRepository.findByUsernameOrEmail(username, username)
                    .orElseThrow(()->new EntityNotFoundException("Compte inexistant"));

            // 3. Vérification détaillée de l'état du compte

            if (appUser.getDeleted() != null && appUser.getDeleted()) {
                handlerExceptionResolver.resolveException(request, response, null,
                        new AccessDeniedException("Votre compte a été supprimé du système."));
                return;
            }

            if (!appUser.getEnabled()) {
                handlerExceptionResolver.resolveException(request, response, null,
                        new AccessDeniedException("Votre compte est désactivé. Veuillez contacter l'administrateur."));
                return;
            }

            if (appUser.getLocked()) {
                handlerExceptionResolver.resolveException(request, response, null,
                        new AccessDeniedException("Votre compte est verrouillé suite à trop de tentatives ou par sécurité."));
                return;
            }

            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("Validation du token échouée : {}", e.getMessage());
            handlerExceptionResolver.resolveException(request, response, null,
                    new org.springframework.security.authentication.BadCredentialsException("Session expirée ou invalide"));
        }
    }


}