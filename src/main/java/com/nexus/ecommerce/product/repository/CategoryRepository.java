package com.nexus.ecommerce.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nexus.ecommerce.product.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsBySlug(String slug);

}
