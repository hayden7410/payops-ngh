package com.payops.backend.auth.dto;

public record LoginResponse(
        String token,
        Long userId,
        String email,
        String displayName
) {
}