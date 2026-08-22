package com.taurustex.api.repositories;

import com.taurustex.api.basis.BaseRepository;
import com.taurustex.api.models.AppPermission;
import org.springframework.stereotype.Repository;

@Repository
public interface AppPermissionRepository extends BaseRepository<AppPermission, String> {
    boolean existsByName(String name);
}
