package com.nexus.ecommerce.product.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
        @NotBlank(message = "Category name is required") String name,

        @NotBlank(message = "Slug is required") String slug) {
}
