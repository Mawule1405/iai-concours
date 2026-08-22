package com.taurustex.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.taurustex.api.basis.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity // N'oublie pas l'annotation @Entity ici
public class AppUser extends BaseEntity {

    private String firstName;
    private String lastName;

    @Column(nullable = false, unique = true)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;


    private String email;

    @Builder.Default
    private Boolean enabled = true;

    @Builder.Default
    private Boolean locked = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @Builder.Default
    private Set<AppRole> roles = new HashSet<>(); // Utilisation de Set pour éviter les doublons

}