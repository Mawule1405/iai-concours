package com.taurustex.api.models;


import com.taurustex.api.basis.BaseEntity;
import jakarta.persistence.Entity;
import lombok.*;

@Setter
@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class AppPermission  extends BaseEntity {
    private String name;
    private String description;
}
