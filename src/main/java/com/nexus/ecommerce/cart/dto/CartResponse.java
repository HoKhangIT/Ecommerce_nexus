package com.nexus.ecommerce.cart.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;

@Builder
public record CartResponse(
        List<CartItemResponse> items,
        BigDecimal totalPrice) {

}
