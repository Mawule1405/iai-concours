package com.taurustex.api.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppRoleDto  {
    private String id;
    private String name;
    private String description;
    private LocalDateTime createdAt;



    Set<AppPermissionDto> permissions = new HashSet<>();
}
