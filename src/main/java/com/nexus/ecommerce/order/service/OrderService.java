package com.nexus.ecommerce.order.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nexus.ecommerce.cart.dto.CartItemResponse;
import com.nexus.ecommerce.cart.dto.CartResponse;
import com.nexus.ecommerce.cart.service.CartService;
import com.nexus.ecommerce.common.config.RabbitMQConfig;
import com.nexus.ecommerce.common.exception.AppException;
import com.nexus.ecommerce.common.exception.ErrorCode;
import com.nexus.ecommerce.inventory.service.InventoryService;
import com.nexus.ecommerce.notification.OrderPlacedEvent;
import com.nexus.ecommerce.order.dto.OrderItemDto;
import com.nexus.ecommerce.order.dto.OrderResponse;
import com.nexus.ecommerce.order.entity.Order;
import com.nexus.ecommerce.order.entity.OrderItem;
import com.nexus.ecommerce.order.repository.OrderRepository;
import com.nexus.ecommerce.product.entity.Product;
import com.nexus.ecommerce.product.repository.ProductRepository;
import com.nexus.ecommerce.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

        private final OrderRepository orderRepository;
        private final ProductRepository productRepository;
        private final InventoryService inventoryService;
        private final CartService cartService;
        private final RabbitTemplate rabbitTemplate;

        @Transactional
        public OrderResponse checkout(User user) {
                CartResponse cart = cartService.getCart(user.getId());
                if (cart.items().isEmpty()) {
                        throw new AppException(ErrorCode.CART_EMPTY);
                }

                String orderCode = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                Order order = Order.builder()
                                .orderCode(orderCode)
                                .user(user)
                                .totalAmount(cart.totalPrice())
                                .status("CONFIRMED")
                                .build();

                // 1. Deduct stock and build order items
                for (CartItemResponse cartItem : cart.items()) {
                        inventoryService.deductStock(cartItem.productId(), cartItem.quantity());

                        Product product = productRepository.findById(cartItem.productId())
                                        .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

                        OrderItem orderItem = OrderItem.builder()
                                        .order(order)
                                        .product(product)
                                        .quantity(cartItem.quantity())
                                        .unitPrice(cartItem.unitPrice())
                                        .build();
                        order.getItems().add(orderItem);
                }

                Order savedOrder = orderRepository.save(order);

                // 2. Clear user's Redis shopping cart
                cartService.clearCart(user.getId());

                // 3. Publish asynchronous event to RabbitMQ
                OrderPlacedEvent event = OrderPlacedEvent.builder()
                                .orderCode(savedOrder.getOrderCode())
                                .userEmail(user.getEmail())
                                .totalAmount(savedOrder.getTotalAmount())
                                .placedAt(LocalDateTime.now())
                                .build();
                rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE, RabbitMQConfig.ORDER_ROUTING_KEY, event);

                return mapToResponse(savedOrder);
        }

        @Transactional(readOnly = true)
        public List<OrderResponse> getMyOrders(Long userId) {
                return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                                .map(this::mapToResponse)
                                .toList();
        }

        private OrderResponse mapToResponse(Order order) {
                List<OrderItemDto> itemDtos = order.getItems().stream()
                                .map(i -> OrderItemDto.builder()
                                                .productId(i.getProduct().getId())
                                                .productName(i.getProduct().getName())
                                                .quantity(i.getQuantity())
                                                .unitPrice(i.getUnitPrice())
                                                .build())
                                .toList();

                return OrderResponse.builder()
                                .id(order.getId())
                                .orderCode(order.getOrderCode())
                                .totalAmount(order.getTotalAmount())
                                .status(order.getStatus())
                                .createdAt(order.getCreatedAt())
                                .items(itemDtos)
                                .build();
        }
}
