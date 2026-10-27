package com.nexus.ecommerce.product.controller;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexus.ecommerce.common.dto.ApiResponse;
import com.nexus.ecommerce.product.dto.ProductCreateRequest;
import com.nexus.ecommerce.product.dto.ProductResponse;
import com.nexus.ecommerce.product.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(@Valid @RequestBody ProductCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(productService.createProduct(request), "Product created"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProductById(id), "Product details"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getAll(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(productService.getAllProducts(pageable), "Product list"));
    }
}
