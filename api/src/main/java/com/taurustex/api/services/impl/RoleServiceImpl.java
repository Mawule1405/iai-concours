package com.taurustex.api.services.impl;


import com.taurustex.api.dtos.AppRoleDto;
import com.taurustex.api.exception.ResourceNotFoundException;
import com.taurustex.api.mappers.RoleMapper;
import com.taurustex.api.models.AppPermission;
import com.taurustex.api.models.AppRole;
import com.taurustex.api.repositories.AppPermissionRepository;
import com.taurustex.api.repositories.AppRoleRepository;
import com.taurustex.api.services.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RoleServiceImpl implements RoleService {

    private final AppRoleRepository roleRepository;
    private final AppPermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    @Override
    public AppRoleDto createRole(AppRoleDto roleDto) {
        log.info("Création d'un nouveau rôle : {}", roleDto.getName());

        AppRole role = roleMapper.toEntity(roleDto);

        // Gestion des permissions à la création
        if (roleDto.getPermissions() != null && !roleDto.getPermissions().isEmpty()) {
            Set<AppPermission> permissions = fetchPermissions(roleDto);
            role.setPermissions(permissions);
        }

        return roleMapper.toDto(roleRepository.save(role));
    }

    @Transactional(readOnly = true)
    @Override
    public List<AppRoleDto> getAllRoles() {
        log.info("Récupération de la liste des rôles");
        return roleMapper.toDtoList(roleRepository.findAll());
    }

    @Transactional(readOnly = true)
    @Override
    public AppRoleDto getRoleById(String id) {
        log.info("Récupération du rôle ID : {}", id);
        return roleRepository.findById(id)
                .map(roleMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Rôle introuvable avec l'ID : " + id));
    }

    @Override
    public AppRoleDto updateRole(String id, AppRoleDto roleDto) {
        log.info("Mise à jour complète du rôle ID : {}", id);

        // 1. On récupère l'existant
        AppRole existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mise à jour impossible : Rôle ID " + id + " inexistant"));

        // 2. Mise à jour des champs simples via MapStruct
        roleMapper.updateEntityFromDto(roleDto, existingRole);

        // 3. Mise à jour manuelle de la collection de permissions
        if (roleDto.getPermissions() != null) {
            Set<AppPermission> updatedPermissions = fetchPermissions(roleDto);

            // On vide et on remplit pour que Hibernate gère correctement la table de jointure
            existingRole.getPermissions().clear();
            existingRole.getPermissions().addAll(updatedPermissions);
        }

        return roleMapper.toDto(roleRepository.save(existingRole));
    }

    @Override
    public void deleteRole(String id) {
        log.info("Suppression du rôle ID : {}", id);
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Suppression impossible : Rôle ID " + id + " inexistant");
        }
        roleRepository.deleteById(id);
    }

    /**
     * Méthode privée pour transformer les DTOs de permissions en entités persistantes
     */
    private Set<AppPermission> fetchPermissions(AppRoleDto roleDto) {
        return roleDto.getPermissions().stream()
                .map(pDto -> permissionRepository.findById(pDto.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Permission ID " + pDto.getId() + " introuvable")))
                .collect(Collectors.toSet());
    }
}