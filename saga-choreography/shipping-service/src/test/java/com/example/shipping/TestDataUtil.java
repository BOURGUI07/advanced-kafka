package com.example.shipping;

import com.example.common.events.order.OrderEvent;

import java.time.Instant;
import java.util.UUID;

public class TestDataUtil {
    public static OrderEvent.Created createOrderCreatedEvent(
            Integer customerId,
            Integer productId,
            Integer unitPrice,
            Integer quantity
    ){
        return OrderEvent.Created.builder()
                .customerId(customerId)
                .productId(productId)
                .unitPrice(unitPrice)
                .quantity(quantity)
                .createdAt(Instant.now())
                .orderId(UUID.randomUUID())
                .totalAmount(unitPrice*quantity)
                .build();
    }

    public static OrderEvent.Cancelled createOrderCancelledEvent(
            UUID orderId
    ){
        return OrderEvent.Cancelled.builder()
                .createdAt(Instant.now())
                .orderId(orderId)
                .build();
    }

    public static OrderEvent.Completed createOrderCompletedEvent(UUID orderId) {
        return OrderEvent.Completed.builder()
                .orderId(orderId)
                .createdAt(Instant.now())
                .build();
    }
}
