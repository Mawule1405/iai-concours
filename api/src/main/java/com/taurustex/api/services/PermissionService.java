package com.taurustex.api.services;



import com.taurustex.api.models.AppPermission;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PermissionService {
    @Transactional(readOnly = true)
    List<AppPermission> getAllPermissions();

    @Transactional(readOnly = true)
    AppPermission getPermissionById(String id);
}
