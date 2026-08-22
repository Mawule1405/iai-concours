package com.taurustex.api.mappers;


import com.taurustex.api.dtos.AppRoleDto;
import com.taurustex.api.models.AppRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {

    // Entité -> DTO (Sortie API)
    AppRoleDto toDto(AppRole role);

    // DTO -> Entité (Entrée API pour création)

    @Mapping(target = "permissions", ignore = true)
    AppRole toEntity(AppRoleDto dto);

    // Conversion de listes (Entités -> DTOs)
    List<AppRoleDto> toDtoList(List<AppRole> roles);

    // Mise à jour d'une entité existante à partir d'un DTO (Très utile pour le CRUD Update)

    @Mapping(target = "permissions", ignore = true)
    void updateEntityFromDto(AppRoleDto dto, @MappingTarget AppRole role);
}