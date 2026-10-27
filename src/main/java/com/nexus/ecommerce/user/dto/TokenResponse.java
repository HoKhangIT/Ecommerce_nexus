package com.nexus.ecommerce.user.dto;

import lombok.Builder;

@Builder
public record TokenResponse(
        String accessToken,
        String refreshToken,
        String role) {
}
