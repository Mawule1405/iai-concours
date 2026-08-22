package com.taurustex.api.repositories;


import com.taurustex.api.basis.BaseRepository;
import com.taurustex.api.models.AppRole;
import org.springframework.stereotype.Repository;

@Repository
public interface AppRoleRepository extends BaseRepository<AppRole, String> {
    boolean existsByName(String name);

    AppRole findByName(String name);
}
