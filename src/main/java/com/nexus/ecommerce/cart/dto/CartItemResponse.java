package com.nexus.ecommerce.cart.dto;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record CartItemResponse(
        Long productId,
        String productName,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal subTotal) {
}
