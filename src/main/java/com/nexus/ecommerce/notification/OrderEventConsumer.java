package com.nexus.ecommerce.notification;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.nexus.ecommerce.common.config.RabbitMQConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OrderEventConsumer {

    @RabbitListener(queues = RabbitMQConfig.ORDER_QUEUE)
    public void handleOrderPlacedEvent(OrderPlacedEvent event) {
        // Simulate asynchronous email sending or invoice generation
        log.info("📧 [ASYNC RABBITMQ WORKER] Sending confirmation email to {} for Order #{} (Total: ${})",
                event.userEmail(), event.orderCode(), event.totalAmount());
    }
}
