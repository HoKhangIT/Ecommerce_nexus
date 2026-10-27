package com.nexus.ecommerce.product.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nexus.ecommerce.common.exception.AppException;
import com.nexus.ecommerce.common.exception.ErrorCode;
import com.nexus.ecommerce.product.dto.CategoryRequest;
import com.nexus.ecommerce.product.dto.CategoryResponse;
import com.nexus.ecommerce.product.entity.Category;
import com.nexus.ecommerce.product.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsBySlug(request.slug())) {
            throw new AppException(ErrorCode.DUPLICATE_RESOURCE);
        }
        Category category = Category.builder()
                .name(request.name())
                .slug(request.slug())
                .build();
        Category saved = categoryRepository.save(category);
        return CategoryResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .slug(saved.getSlug())
                .build();
    }

    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream()
                .map(c -> CategoryResponse.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .slug(c.getSlug())
                        .build())
                .toList();
    }
}
