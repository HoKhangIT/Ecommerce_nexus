package com.nexus.ecommerce.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;

@Builder
public record OrderResponse(
        Long id,
        String orderCode,
        BigDecimal totalAmount,
        String status,
        LocalDateTime createdAt,
        List<OrderItemDto> items) {
}
