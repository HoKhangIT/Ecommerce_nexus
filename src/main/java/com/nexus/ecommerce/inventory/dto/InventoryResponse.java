package com.nexus.ecommerce.inventory.dto;

import lombok.Builder;

@Builder
public record InventoryResponse(
        Long productId,
        Integer availableQuantity,
        Integer reservedQuantity,
        Long version) {
}
