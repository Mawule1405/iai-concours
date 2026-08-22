package com.taurustex.api.dtos;

public record PasswordChangeRequest(
        String emailOrUsername,
        String oldPassword,
        String newPassword
) {}