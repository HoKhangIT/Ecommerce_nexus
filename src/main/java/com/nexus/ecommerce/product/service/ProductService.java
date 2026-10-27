package com.nexus.ecommerce.product.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nexus.ecommerce.common.exception.AppException;
import com.nexus.ecommerce.common.exception.ErrorCode;
import com.nexus.ecommerce.inventory.entity.Inventory;
import com.nexus.ecommerce.inventory.repository.InventoryRepository;
import com.nexus.ecommerce.product.dto.ProductCreateRequest;
import com.nexus.ecommerce.product.dto.ProductResponse;
import com.nexus.ecommerce.product.entity.Category;
import com.nexus.ecommerce.product.entity.Product;
import com.nexus.ecommerce.product.repository.CategoryRepository;
import com.nexus.ecommerce.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        if (productRepository.existsBySku(request.sku())) {
            throw new AppException(ErrorCode.DUPLICATE_RESOURCE);
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Product product = Product.builder()
                .category(category)
                .sku(request.sku())
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .status("ACTIVE")
                .build();

        Product savedProduct = productRepository.save(product);

        // Initialize inventory record for the new product
        Inventory inventory = Inventory.builder()
                .product(savedProduct)
                .availableQuantity(request.initialStock())
                .reservedQuantity(0)
                .build();
        inventoryRepository.save(inventory);

        return mapToResponse(savedProduct, category.getName(), request.initialStock());
    }

    // Cache product details in Redis under key "products::{id}"
    @Cacheable(value = "products", key = "#id")
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        log.info("Fetching product ID {} from PostgreSQL (Cache Miss)...", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        Inventory inventory = inventoryRepository.findByProductId(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        return mapToResponse(product, product.getCategory().getName(), inventory.getAvailableQuantity());
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(product -> {
            int stock = inventoryRepository.findByProductId(product.getId())
                    .map(Inventory::getAvailableQuantity)
                    .orElse(0);
            return mapToResponse(product, product.getCategory().getName(), stock);
        });
    }

    private ProductResponse mapToResponse(Product product, String categoryName, Integer stock) {
        return ProductResponse.builder()
                .id(product.getId())
                .categoryId(product.getCategory().getId())
                .categoryName(categoryName)
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .status(product.getStatus())
                .availableStock(stock)
                .build();
    }
}
