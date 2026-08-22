package com.taurustex.api.services;


import com.taurustex.api.dtos.AppUserDto;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AppUserService {
    AppUserDto createUser(AppUserDto userDto);

    @Transactional(readOnly = true)
    List<AppUserDto> getAllUsers();

    Page<AppUserDto> getAllUsers(String keyword,int page,int size);

    @Transactional(readOnly = true)
    AppUserDto getUserById(String id);

    AppUserDto updateUser(String id, AppUserDto userDto);

    void resetPassword(String id);

    void deleteUser(String id);

    boolean getNewPassword(String emailOrUsername);

    @Transactional
    boolean getNewPassword(String emailOrUsername, String oldPassword, String newPassword);

    void toggleStatus(String id);

    void toggleLock(String id);

    @Nullable AppUserDto getUserByUsername(String name);
}
