package com.taurustex.api.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {
    private int code;
    private String message;
    private Object details; // Changé en Object pour accepter Map ou String
    private LocalDateTime timestamp;

    public ApiError(int code, String message, Object details) {
        this.code = code;
        this.message = message;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
}