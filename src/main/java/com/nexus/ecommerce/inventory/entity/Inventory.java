package com.nexus.ecommerce.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import com.nexus.ecommerce.common.entity.BaseEntity;
import com.nexus.ecommerce.product.entity.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inventories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity;

    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity;

    // Optimistic locking version column to prevent overselling under high
    // concurrency
    @Version
    @Column(nullable = false)
    private Long version;
}
