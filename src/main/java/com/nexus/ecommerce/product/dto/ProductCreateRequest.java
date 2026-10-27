package com.nexus.ecommerce.product.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductCreateRequest(
        @NotNull(message = "Category ID is required") Long categoryId,

        @NotBlank(message = "SKU is required") String sku,

        @NotBlank(message = "Product name is required") String name,

        String description,

        @NotNull(message = "Price is required") @DecimalMin(value = "0.0", message = "Price must be >= 0") BigDecimal price,

        @NotBlank(message = "initial stock is required") @Min(value = 0, message = "Initial stock must be >= 0") Integer initialStock) {

}
