package com.taurustex.api.models;

import com.taurustex.api.basis.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppRole extends BaseEntity {
    private String name;
    private String description;

    @ManyToMany
    @Builder.Default
    Set<AppPermission> permissions = new HashSet<>();
}
