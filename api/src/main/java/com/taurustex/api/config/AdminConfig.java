package com.taurustex.api.config;



import com.taurustex.api.models.AppRole;
import com.taurustex.api.models.AppUser;
import com.taurustex.api.repositories.AppRoleRepository;
import com.taurustex.api.repositories.AppUserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
@DependsOn("initializePermission")
public class AdminConfig {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppRoleRepository appRoleRepository;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.first-name}")
    private String adminFirstName;

    @Value("${app.admin.last-name}")
    private String adminLastName;


    @PostConstruct
    @Order(2)
    public void createSuperAdmin() {
        // On vérifie si l'admin existe déjà pour éviter les doublons au redémarrage
        if (!appUserRepository.existsByUsername(adminUsername)) {
            log.info("Initialisation du Super Admin à partir des propriétés...");

            AppRole role = appRoleRepository.findByName("ROLE_ADMINISTRATOR");
            AppUser admin = new AppUser();
            admin.setUsername(adminUsername);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setEmail(adminEmail);
            admin.setFirstName(adminFirstName);
            admin.setLastName(adminLastName);
            admin.setRoles(Set.of(role));
            appUserRepository.save(admin);
            log.info("Super Administrateur '{}' créé avec succès !", adminUsername);
        } else {
            log.debug("Le Super Administrateur '{}' existe déjà en base.", adminUsername);
        }
    }
}