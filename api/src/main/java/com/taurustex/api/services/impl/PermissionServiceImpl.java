package com.taurustex.api.services.impl;


import com.taurustex.api.models.AppPermission;
import com.taurustex.api.repositories.AppPermissionRepository;
import com.taurustex.api.services.PermissionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PermissionServiceImpl implements PermissionService {

    private final AppPermissionRepository permissionRepository;

    /**
     * Retourne toutes les permissions enregistrées en base de données.
     * Utile pour l'interface d'administration (assignation de permissions aux rôles).
     */
    @Transactional(readOnly = true)
    @Override
    public List<AppPermission> getAllPermissions() {
        log.info("Récupération de toutes les permissions");
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        return permissionRepository.findAll(sort);
    }

    /**
     * Retourne une permission spécifique par son ID (UUID String).
     * @throws EntityNotFoundException si l'ID n'existe pas.
     */
    @Transactional(readOnly = true)
    @Override
    public AppPermission getPermissionById(String id) {
        log.info("Recherche de la permission avec l'ID : {}", id);
        return permissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Permission introuvable avec l'ID : " + id));
    }

}