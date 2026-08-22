package com.taurustex.api.services;


import com.taurustex.api.dtos.AppRoleDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RoleService {
    AppRoleDto createRole(AppRoleDto roleDto);

    @Transactional(readOnly = true)
    List<AppRoleDto> getAllRoles();

    @Transactional(readOnly = true)
    AppRoleDto getRoleById(String id);

    AppRoleDto updateRole(String id, AppRoleDto roleDto);

    void deleteRole(String id);
}
