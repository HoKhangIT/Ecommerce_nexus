package com.nexus.ecommerce.notification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record OrderPlacedEvent(
        String orderCode,
        String userEmail,
        BigDecimal totalAmount,
        LocalDateTime placedAt) {
}
