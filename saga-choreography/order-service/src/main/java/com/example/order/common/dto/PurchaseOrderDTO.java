package com.example.order.common.dto;

import com.example.common.events.order.OrderStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record PurchaseOrderDTO(
         UUID orderId,
         Integer customerId,
         Integer productId,
         Integer quantity,
         Integer unitPrice,
         Integer amount,
         OrderStatus status,
         Instant deliveryDate
) {
}
