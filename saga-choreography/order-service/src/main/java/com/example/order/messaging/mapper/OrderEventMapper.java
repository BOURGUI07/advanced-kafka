package com.example.order.messaging.mapper;

import com.example.common.events.order.OrderEvent;
import com.example.order.common.dto.PurchaseOrderDTO;
import org.springframework.stereotype.Component;

import java.time.Instant;

public class OrderEventMapper {
    public static OrderEvent toOrderCreatedEvent(PurchaseOrderDTO dto) {
        return OrderEvent.Created.builder()
                .orderId(dto.orderId())
                .unitPrice(dto.unitPrice())
                .quantity(dto.quantity())
                .productId(dto.productId())
                .totalAmount(dto.amount())
                .customerId(dto.customerId())
                .createdAt(Instant.now())
                .build();
    }

    public static OrderEvent toOrderCancelledEvent(PurchaseOrderDTO dto) {
        return OrderEvent.Cancelled.builder()
                .orderId(dto.orderId())
                .createdAt(Instant.now())
                .build();
    }

    public static OrderEvent toOrderCompletedEvent(PurchaseOrderDTO dto) {
        return OrderEvent.Completed.builder()
                .orderId(dto.orderId())
                .createdAt(Instant.now())
                .build();
    }
}
