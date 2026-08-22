package com.taurustex.api.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUserDto  {

    private String id;
    private String firstName;
    private String lastName;
    @NotBlank(message = "Il faut obligatoirement un username")
    private String username;
    private String picture;
    private String email;
    private Boolean enabled;
    private Boolean locked;
    private LocalDateTime createdAt;
    private Set<AppRoleDto> roles = new HashSet<>();
}