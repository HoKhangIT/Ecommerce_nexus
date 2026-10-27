package com.nexus.ecommerce.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nexus.ecommerce.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsBySku(String sku);
}
