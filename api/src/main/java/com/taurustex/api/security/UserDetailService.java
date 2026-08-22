package com.taurustex.api.security;


import com.taurustex.api.models.AppPermission;
import com.taurustex.api.models.AppUser;
import com.taurustex.api.repositories.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserDetailService implements UserDetailsService {

    private final AppUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        log.info("Chargement de l'utilisateur : {}", usernameOrEmail);

        AppUser appUser = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + usernameOrEmail));

        // Vérifications de sécurité
        if (appUser.getDeleted() ) {
            throw new UsernameNotFoundException("Ce compte a été supprimé.");
        }
        if (!appUser.getEnabled()) {
            throw new UsernameNotFoundException("Ce compte est désactivé.");
        }
        if (appUser.getLocked()) {
            throw new UsernameNotFoundException("Ce compte est verrouillé.");
        }

        // --- GESTION DES AUTORITÉS (Rôles + Permissions) ---
        // On utilise un Set pour éliminer automatiquement les doublons
        Set<GrantedAuthority> authorities = appUser.getRoles().stream()
                .flatMap(role -> {
                    // 1. On récupère le rôle (ex: ROLE_ADMIN)
                    Stream<String> roleStream = Stream.of( role.getName());

                    // 2. On récupère toutes les permissions de ce rôle (ex: PERM_CREATE_USER)
                    Stream<String> permissionStream = role.getPermissions().stream()
                            .map(AppPermission::getName);

                    // On fusionne les deux pour ce rôle précis
                    return Stream.concat(roleStream, permissionStream);
                })
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        log.debug("Utilisateur {} chargé avec {} autorités", appUser.getUsername(), authorities.size());

        return User.builder()
                .username(appUser.getUsername())
                .password(appUser.getPassword())
                .authorities(authorities)
                .accountLocked(appUser.getLocked())
                .disabled(!appUser.getEnabled())
                .accountExpired(false)
                .credentialsExpired(false)
                .build();
    }
}