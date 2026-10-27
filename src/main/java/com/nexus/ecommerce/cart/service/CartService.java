package com.nexus.ecommerce.cart.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.nexus.ecommerce.cart.dto.CartItemRequest;
import com.nexus.ecommerce.cart.dto.CartItemResponse;
import com.nexus.ecommerce.cart.dto.CartResponse;
import com.nexus.ecommerce.product.dto.ProductResponse;
import com.nexus.ecommerce.product.service.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

    private final StringRedisTemplate redisTemplate;
    private final ProductService productService;

    private static final String CART_PREFIX = "cart:";
    private static final Duration CART_TTL = Duration.ofDays(7);

    public CartResponse addToCart(Long userId, CartItemRequest request) {
        // Validate product existence (leverages Redis Cache from Sprint 3)
        productService.getProductById(request.productId());

        String key = CART_PREFIX + userId;
        String field = String.valueOf(request.productId());

        // Atomically increment item quantity inside Redis Hash
        redisTemplate.opsForHash().increment(key, field, request.quantity());
        redisTemplate.expire(key, CART_TTL);

        return getCart(userId);
    }

    public CartResponse getCart(Long userId) {
        String key = CART_PREFIX + userId;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);

        List<CartItemResponse> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            Long productId = Long.valueOf(entry.getKey().toString());
            int quantity = Integer.parseInt(entry.getValue().toString());

            ProductResponse product = productService.getProductById(productId);
            BigDecimal subTotal = product.price().multiply(BigDecimal.valueOf(quantity));
            total = total.add(subTotal);

            items.add(CartItemResponse.builder()
                    .productId(productId)
                    .productName(product.name())
                    .unitPrice(product.price())
                    .quantity(quantity)
                    .subTotal(subTotal)
                    .build());
        }

        return CartResponse.builder()
                .items(items)
                .totalPrice(total)
                .build();
    }

    public void removeItem(Long userId, Long productId) {
        redisTemplate.opsForHash().delete(CART_PREFIX + userId, String.valueOf(productId));
    }

    public void clearCart(Long userId) {
        redisTemplate.delete(CART_PREFIX + userId);
    }
}
