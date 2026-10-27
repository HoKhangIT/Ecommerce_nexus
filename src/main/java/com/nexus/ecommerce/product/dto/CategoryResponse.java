package com.nexus.ecommerce.product.dto;

import lombok.Builder;

@Builder
public record CategoryResponse(
        Long id,
        String name,
        String slug) {
}
