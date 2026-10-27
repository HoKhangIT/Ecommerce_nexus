package com.nexus.ecommerce.inventory.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexus.ecommerce.common.dto.ApiResponse;
import com.nexus.ecommerce.inventory.dto.InventoryResponse;
import com.nexus.ecommerce.inventory.dto.StockUpdateRequest;
import com.nexus.ecommerce.inventory.service.InventoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/{productId}/add-stock")
    public ResponseEntity<ApiResponse<InventoryResponse>> addStock(
            @PathVariable Long productId,
            @Valid @RequestBody StockUpdateRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        inventoryService.addStock(productId, request.quantity()),
                        "Stock added successfully"));
    }
}
