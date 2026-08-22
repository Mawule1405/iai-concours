package com.taurustex.api.controllers;


import com.taurustex.api.models.AppPermission;
import com.taurustex.api.services.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/iai-concours-api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    /**
     * Récupère la liste de toutes les permissions disponibles dans le système.
     * Accessible uniquement par les administrateurs ou via une permission spécifique.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('PERM_MANAGE_ROLES')")
    public ResponseEntity<List<AppPermission>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

    /**
     * Récupère les détails d'une permission spécifique par son ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_MANAGE_ROLES')")
    public ResponseEntity<AppPermission> getPermissionById(@PathVariable String id) {
        return ResponseEntity.ok(permissionService.getPermissionById(id));
    }
}