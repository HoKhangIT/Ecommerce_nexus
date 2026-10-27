package com.nexus.ecommerce.inventory.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nexus.ecommerce.common.exception.AppException;
import com.nexus.ecommerce.common.exception.ErrorCode;
import com.nexus.ecommerce.inventory.dto.InventoryResponse;
import com.nexus.ecommerce.inventory.entity.Inventory;
import com.nexus.ecommerce.inventory.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    @CacheEvict(value = "products", key = "#productId") // Clear product cache when stock changes
    public InventoryResponse addStock(Long productId, int quantityToAdd) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + quantityToAdd);
        Inventory saved = inventoryRepository.save(inventory);

        return InventoryResponse.builder()
                .productId(productId)
                .availableQuantity(saved.getAvailableQuantity())
                .reservedQuantity(saved.getReservedQuantity())
                .version(saved.getVersion())
                .build();
    }

    // Deduct stock safely using @Version optimistic lock (used during Order
    // Checkout)
    @Transactional
    @CacheEvict(value = "products", key = "#productId")
    public void deductStock(Long productId, int quantityToDeduct) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (inventory.getAvailableQuantity() < quantityToDeduct) {
            throw new AppException(ErrorCode.OUT_OF_STOCK);
        }

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - quantityToDeduct);
        inventoryRepository.save(inventory);
    }
}
