package com.taurustex.api.controllers;


import com.taurustex.api.dtos.AppRoleDto;
import com.taurustex.api.services.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/iai-concours-api/roles")
@RequiredArgsConstructor
@Slf4j
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @PreAuthorize("hasAuthority('PERM_MANAGE_ROLES')")
    public ResponseEntity<AppRoleDto> createRole(@Valid @RequestBody AppRoleDto roleDto) {
        log.info("REST Request - Création d'un rôle : {}", roleDto.getName());
        AppRoleDto createdRole = roleService.createRole(roleDto);
        return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PERM_MANAGE_ROLES')")
    public ResponseEntity<List<AppRoleDto>> getAllRoles() {
        log.info("REST Request - Récupération de tous les rôles");
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_MANAGE_ROLES')")
    public ResponseEntity<AppRoleDto> getRoleById(@PathVariable String id) {
        log.info("REST Request - Récupération du rôle ID : {}", id);
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_MANAGE_ROLES')")
    public ResponseEntity<AppRoleDto> updateRole(@PathVariable String id, @Valid @RequestBody AppRoleDto roleDto) {
        log.info("REST Request - Mise à jour du rôle ID : {}", id);
        return ResponseEntity.ok(roleService.updateRole(id, roleDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_MANAGE_ROLES')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRole(@PathVariable String id) {
        log.info("REST Request - Suppression du rôle ID : {}", id);
        roleService.deleteRole(id);
    }
}