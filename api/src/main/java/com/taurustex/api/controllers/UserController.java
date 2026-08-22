package com.taurustex.api.controllers;


import com.taurustex.api.dtos.AppUserDto;
import com.taurustex.api.dtos.Pagination;
import com.taurustex.api.dtos.PasswordChangeRequest;
import com.taurustex.api.services.AppUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/iai-concours-api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final AppUserService userService;

    @PostMapping
    @PreAuthorize("hasAuthority('PERM_CREATE_USER')")
    public ResponseEntity<AppUserDto> createUser(@Valid @RequestBody AppUserDto userDto) {
        log.info("REST Request - Création d'un utilisateur : {}", userDto.getUsername());
        AppUserDto createdUser = userService.createUser(userDto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PERM_READ_USER')")
    public ResponseEntity<Pagination<AppUserDto>> getAllUsers(
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        log.info("REST Request - Liste des utilisateurs par pagination");
        return ResponseEntity.ok(Pagination.of(userService.getAllUsers(keyword, page, size)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_READ_USER')")
    public ResponseEntity<AppUserDto> getUserById(@PathVariable String id) {
        log.info("REST Request - Récupération de l'utilisateur ID : {}", id);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/my-account")
    public ResponseEntity<AppUserDto> getUser(Principal principal) {
        return ResponseEntity.ok(userService.getUserByUsername(principal.getName()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_UPDATE_USER')")
    public ResponseEntity<AppUserDto> updateUser(@PathVariable String id, @Valid @RequestBody AppUserDto userDto) {
        log.info("REST Request - Mise à jour de l'utilisateur ID : {}", id);
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }

    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasAuthority('PERM_UPDATE_USER')")
    public ResponseEntity<Void> resetPassword(@PathVariable String id) {
        log.info("REST Request - Réinitialisation du mot de passe pour l'utilisateur ID : {}", id);
        userService.resetPassword(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_DELETE_USER')")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        log.info("REST Request - Suppression de l'utilisateur ID : {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/toggle-status")
    @PreAuthorize("hasAuthority('PERM_UPDATE_USER')")
    public ResponseEntity<Void> toggleUserStatus(@RequestBody String id) {
        userService.toggleStatus(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/toggle-lock")
    @PreAuthorize("hasAuthority('PERM_UPDATE_USER')")
    public ResponseEntity<Void> toggleUserLock(@RequestBody String id) {
        userService.toggleLock(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/new-password")
    public ResponseEntity<Boolean> getNewPassword(@RequestBody PasswordChangeRequest request) {
        boolean result = userService.getNewPassword(
                request.emailOrUsername(),
                request.oldPassword(),
                request.newPassword()
        );
        return ResponseEntity.ok(result);
    }

}