package com.taurustex.api.services.impl;


import com.taurustex.api.dtos.AppUserDto;
import com.taurustex.api.exception.ResourceNotFoundException;
import com.taurustex.api.mappers.AppUserMapper;
import com.taurustex.api.models.AppRole;
import com.taurustex.api.models.AppUser;
import com.taurustex.api.repositories.AppRoleRepository;
import com.taurustex.api.repositories.AppUserRepository;
import com.taurustex.api.services.AppUserService;
import com.taurustex.api.tools.emails.EmailService;
import com.taurustex.api.utils.GeneratePasswordUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository userRepository;
    private final AppRoleRepository roleRepository;
    private final AppUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;



    @Override
    public AppUserDto createUser(AppUserDto userDto) {
        log.info("Création de l'utilisateur : {}", userDto.getUsername());
        AppUser user = userMapper.toEntity(userDto);

        String rawPassword = GeneratePasswordUtil.generateRandomPassword();
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRoles(fetchRoles(userDto));
        user.setEnabled(true);
        user.setLocked(false);

        AppUser savedUser = userRepository.save(user);

        try {

            emailService.sendUserInformation(user.getEmail(), user.getFirstName() + " " + user.getLastName(),
                    user.getUsername(), rawPassword, "");
        } catch (MessagingException e) {
            log.error("Erreur envoi email création : {}", e.getMessage());
        }

        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppUserDto> getAllUsers() {
        return userMapper.toDtoList(userRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppUserDto> getAllUsers(String keyword, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "lastName", "firstName", "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findByKeyword(keyword, pageable).map(userMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public AppUserDto getUserById(String id) {
        return userRepository.findById(id).map(userMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
    }

    @Override
    public AppUserDto updateUser(String id, AppUserDto userDto) {
        AppUser existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur ID " + id + " inexistant"));

        if (userDto.getUsername() != null) existingUser.setUsername(userDto.getUsername().trim());
        if (userDto.getFirstName() != null) existingUser.setFirstName(userDto.getFirstName().trim());
        if (userDto.getLastName() != null) existingUser.setLastName(userDto.getLastName().trim());
        if (userDto.getEmail() != null) existingUser.setEmail(userDto.getEmail().trim());
        if (userDto.getEnabled() != null) existingUser.setEnabled(userDto.getEnabled());
        if (userDto.getLocked() != null) existingUser.setLocked(userDto.getLocked());

        if (userDto.getRoles() != null) {
            existingUser.getRoles().clear();
            existingUser.getRoles().addAll(fetchRoles(userDto));
        }
        return userMapper.toDto(userRepository.save(existingUser));
    }

    @Override
    public void resetPassword(String id) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        String newPassword = GeneratePasswordUtil.generateRandomPassword();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        try {

            emailService.sendPasswordResetInfo(user.getEmail(), user.getFirstName() + " " + user.getLastName(),
                    newPassword, "");
        } catch (MessagingException e) {
            log.error("Erreur envoi email reset : {}", e.getMessage());
        }
    }

    @Override
    public boolean getNewPassword(String emailOrUsername, String oldPassword, String newPassword) {
        AppUser user = userRepository.findByUsernameOrEmail(emailOrUsername, emailOrUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("L'ancien mot de passe est incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    @Override
    @Transactional
    public boolean getNewPassword(String emailOrUsername) {
        // 1. Recherche de l'utilisateur
        AppUser user = userRepository.findByUsernameOrEmail(emailOrUsername, emailOrUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec cet identifiant"));

        // 2. Génération d'un mot de passe aléatoire
        String newRawPassword = GeneratePasswordUtil.generateRandomPassword();

        // 3. Mise à jour du mot de passe dans la base (encodé)
        user.setPassword(passwordEncoder.encode(newRawPassword));
        userRepository.save(user);

        // 4. Envoi par e-mail
        try {
            emailService.sendPasswordResetInfo(
                    user.getEmail(),
                    user.getFirstName() + " " + user.getLastName(),
                    newRawPassword,""
            );
            log.info("Nouveau mot de passe envoyé avec succès à : {}", user.getEmail());
            return true;
        } catch (MessagingException e) {
            log.error("Échec de l'envoi de l'email de réinitialisation pour : {}", user.getEmail());
            // Optionnel : lever une exception si l'envoi est critique pour ton workflow
            return false;
        }
    }
    @Override
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) throw new ResourceNotFoundException("Utilisateur non trouvé");
        userRepository.deleteById(id);
    }



    @Override
    public void toggleStatus(String id) {
        AppUser user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("ID inexistant"));
        user.setEnabled(!(user.getEnabled() != null && user.getEnabled()));
        userRepository.save(user);
    }

    @Override
    public void toggleLock(String id) {
        AppUser user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("ID inexistant"));
        user.setLocked(!(user.getLocked() != null && user.getLocked()));
        userRepository.save(user);
    }

    @Override
    public @Nullable AppUserDto getUserByUsername(String name) {
        AppUser user = userRepository.findByUsername(name).orElseThrow(
                ()->new RuntimeException("User not found"));
        return userMapper.toDto(user);
    }


    private Set<AppRole> fetchRoles(AppUserDto dto) {
        return dto.getRoles().stream()
                .map(r -> roleRepository.findById(r.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Rôle " + r.getId() + " non trouvé")))
                .collect(Collectors.toSet());
    }
}