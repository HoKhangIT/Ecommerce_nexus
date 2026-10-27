package com.nexus.ecommerce.product.dto;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record ProductResponse(
        Long id,
        Long categoryId,
        String categoryName,
        String sku,
        String name,
        String description,
        BigDecimal price,
        String status,
        Integer availableStock) {
}
