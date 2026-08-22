package com.taurustex.api.config;


import com.taurustex.api.enums.EnumPermission;
import com.taurustex.api.models.AppPermission;
import com.taurustex.api.models.AppRole;
import com.taurustex.api.repositories.AppPermissionRepository;
import com.taurustex.api.repositories.AppRoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitializePermission {

    private final AppPermissionRepository permissionRepository;
    private final AppRoleRepository roleRepository;

    @PostConstruct
    @Order(1)
    @Transactional // CRITIQUE : Assure que toute l'init se fait dans une seule unité de travail
    public void initPermissions() {
        log.info("Vérification et initialisation des permissions système...");

        // 1. Initialisation des permissions à partir de l'Enum
        Arrays.stream(EnumPermission.values()).forEach(enumPerm -> {
            if (!permissionRepository.existsByName(enumPerm.name())) {
                log.info("Création de la permission : {}", enumPerm.name());
                AppPermission permission = AppPermission.builder()
                        .name(enumPerm.name()) // Utilise .name() ou .toString() selon ton Enum
                        .description(enumPerm.getDescription())
                        .build();
                permissionRepository.save(permission);
            }
        });

        // 2. Initialisation du rôle Admin s'il n'existe pas
        if (!roleRepository.existsByName("ROLE_ADMINISTRATOR")) {
            log.info("Création du rôle ROLE_ADMINISTRATOR et assignation des permissions...");

            // On récupère toutes les permissions fraîchement créées ou existantes
            List<AppPermission> allPermissions = permissionRepository.findAll();

            AppRole role = AppRole.builder()
                    .name("ROLE_ADMINISTRATOR")
                    .description("Administrateur en charge de toutes les actions")
                    .permissions(new HashSet<>(allPermissions)) // Conversion en Set
                    .build();

            roleRepository.save(role);
            log.info("Rôle ROLE_ADMINISTRATOR créé avec succès.");
        }
    }
}