package com.nexus.ecommerce.user.dto;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record UserResponse(
        Long id,
        String email,
        String fullName,
        String role,
        LocalDateTime createdAt) {
}
