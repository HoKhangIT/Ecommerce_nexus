package com.nexus.ecommerce.order.dto;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record OrderItemDto(
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice) {
}
