package com.example.order.common.dto;

import com.example.common.events.inventory.InventoryStatus;
import com.example.common.events.payment.PaymentStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderPaymentDTO(
        UUID orderId,
        UUID paymentId,
        String message,
        PaymentStatus status
) {
}
