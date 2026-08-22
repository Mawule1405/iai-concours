package com.taurustex.api.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.taurustex.api.exception.InvalidInputException;
import com.taurustex.api.exception.ResourceNotFoundException;
import com.taurustex.api.models.AppPermission;
import com.taurustex.api.models.AppUser;
import com.taurustex.api.repositories.AppUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping(path = "/iai-concours-api/auth/refresh") // Chemin cohérent avec ton Filter
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenController {

    private final UserDetailService userDetailService;
    private final AppUserRepository appUserRepository;
    private final JwtUtil jwtUtil;


    @Value("${https.mode:false}")
    private boolean httpsMode;

    @PostMapping
    public ResponseEntity<Map<String, String>> refreshToken(
            @CookieValue(name = "refresh", required = false) String refreshToken,
            HttpServletRequest request) {

        // 1. On lance une exception si le cookie manque -> Capturé par InvalidInputException ou handleGlobal
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new InvalidInputException("Le cookie de rafraîchissement est absent.");
        }

        try {
            // 2. Validation du Token
            Algorithm algorithm = Algorithm.HMAC512(jwtUtil.getSecret());
            JWTVerifier jwtVerifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = jwtVerifier.verify(refreshToken);

            String username = decodedJWT.getSubject();

            AppUser appUser = appUserRepository.findByUsernameOrEmail(username, username)
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

            if (appUser.getLocked()) {
                throw new AccessDeniedException("Le compte est bloqué ou inactif");
            }

            if (!appUser.getEnabled()) {
                throw new AccessDeniedException("Le compte est inactif");
            }

            User user = (User) userDetailService.loadUserByUsername(username);
            // 1. Récupérer les autorités (Rôles + Permissions)
            Set<SimpleGrantedAuthority> authorities = appUser.getRoles().stream()
                    .flatMap(role -> {
                        // Fusion du nom du rôle et des noms de ses permissions
                        Stream<String> roleName = Stream.of(role.getName());
                        Stream<String> permissions = role.getPermissions().stream()
                                .map(AppPermission::getName);

                        return Stream.concat(roleName, permissions);
                    })
                    .distinct() // Évite les doublons si plusieurs rôles partagent une permission
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());

            // 2. Créer une liste de Strings pour le JWT (ce que JwtUtil attend probablement)
            List<String> rolesAndPermissions = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();


            // 3. Génération des nouveaux tokens
            TokenResponse tokens = jwtUtil.generateTokens(user,  rolesAndPermissions, request);

            // 4. Rotation du Cookie
            ResponseCookie newCookie = ResponseCookie.from("refresh", tokens.getRefresh())
                    .httpOnly(true)
                    .secure(httpsMode)
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60)
                    .sameSite("Lax")
                    .build();

            log.info("Access Token renouvelé pour : {}", username);

            // 5. On retourne une ResponseEntity.
            // Le header Set-Cookie est ajouté à la réponse.
            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.SET_COOKIE, newCookie.toString())
                    .body(Map.of("access", tokens.getAccess()));

        } catch (Exception e) {
            log.error("Erreur refresh token : {}", e.getMessage());
            // On relance l'exception pour que le GlobalExceptionHandler la traite
            // Si c'est une expiration JWT, on peut lancer une BadCredentialsException ou AccessDeniedException
            throw new AccessDeniedException("Session expirée, veuillez vous reconnecter");
        }
    }
}