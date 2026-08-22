package com.taurustex.api.mappers;


import com.taurustex.api.dtos.AppUserDto;
import com.taurustex.api.models.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {RoleMapper.class}) // On utilise RoleMapper pour convertir les rôles à l'intérieur de l'utilisateur
public interface AppUserMapper {

    // Entité -> DTO (Sortie API)
    AppUserDto toDto(AppUser user);

    // DTO -> Entité (Entrée API)

    @Mapping(target = "roles", ignore = true) // On gère les rôles manuellement dans le service pour la persistence
    @Mapping(target = "password", ignore = true) // Le mot de passe est souvent géré via BCrypt séparément
    AppUser toEntity(AppUserDto dto);

    // Liste de conversion
    List<AppUserDto> toDtoList(List<AppUser> users);

    // Mise à jour d'un utilisateur existant
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateEntityFromDto(AppUserDto dto, @MappingTarget AppUser user);
}