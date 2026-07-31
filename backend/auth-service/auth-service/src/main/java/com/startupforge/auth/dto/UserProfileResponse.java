package com.startupforge.auth.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UserProfileResponse(
        UUID id,
        String name,
        String email,
        String role,
        boolean emailVerified,
        LocalDateTime createdAt
) {
}
