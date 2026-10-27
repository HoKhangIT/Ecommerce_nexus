package com.nexus.ecommerce.order.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexus.ecommerce.common.dto.ApiResponse;
import com.nexus.ecommerce.order.dto.OrderResponse;
import com.nexus.ecommerce.order.service.OrderService;
import com.nexus.ecommerce.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        orderService.checkout(userDetails.getUser()),
                        "Order placed successfully"));
    }

    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                orderService.getMyOrders(userDetails.getUser().getId()),
                "Order history retrieved"));
    }
}
