package com.nexus.ecommerce.cart.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexus.ecommerce.cart.dto.CartItemRequest;
import com.nexus.ecommerce.cart.dto.CartResponse;
import com.nexus.ecommerce.cart.service.CartService;
import com.nexus.ecommerce.common.dto.ApiResponse;
import com.nexus.ecommerce.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CartItemRequest request) {
        Long userId = userDetails.getUser().getId();
        return ResponseEntity.ok(ApiResponse.success(cartService.addToCart(userId, request), "Item added to cart"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        return ResponseEntity.ok(ApiResponse.success(cartService.getCart(userId), "Cart retrieved"));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long productId) {
        cartService.removeItem(userDetails.getUser().getId(), productId);
        return ResponseEntity.ok(ApiResponse.success(null, "Item removed from cart"));
    }
}
