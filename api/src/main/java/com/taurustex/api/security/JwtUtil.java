package com.taurustex.api.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${tsc.app.jwt.secret}")
    private String secretKey;
    @Value("${tsc.app.access.time}")
    private Long expireAccess;
    @Value("${tsc.app.refresh.time}")
    private Long expireRefresh;

    public static final List<String> PATHS = List.of("/login","/error","/iai-concours-api/users/register"
            ,"/iai-concours-api/auth/refresh", "/iai-concours-api/public");

    // Suppression du static pour une meilleure testabilité

    public final String getSecret() { return secretKey; }

    public static final String AUTH_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    public TokenResponse generateTokens(User user,  List<String> roles, HttpServletRequest request) {
        Algorithm algorithm = Algorithm.HMAC512(secretKey);
        long now = System.currentTimeMillis();

        String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(now + expireAccess))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", roles)
                .sign(algorithm);

        String refreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(now + expireRefresh))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);

        return TokenResponse.builder()
                .access(accessToken)
                .refresh(refreshToken)
                .build();
    }

    public static  boolean isPermittedPath(String path){

        for(String paths : PATHS){
            if(path.equals(paths) || path.startsWith(paths)){
                return true;
            }
        }
        return false;
    }
}