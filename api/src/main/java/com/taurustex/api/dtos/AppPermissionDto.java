package com.taurustex.api.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppPermissionDto   {
    private String id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
}
