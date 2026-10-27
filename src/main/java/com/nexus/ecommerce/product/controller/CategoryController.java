package com.nexus.ecommerce.product.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexus.ecommerce.common.dto.ApiResponse;
import com.nexus.ecommerce.product.dto.CategoryRequest;
import com.nexus.ecommerce.product.dto.CategoryResponse;
import com.nexus.ecommerce.product.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(categoryService.create(request), "Category created"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getAll(), "Categories retrieved"));
    }
}
