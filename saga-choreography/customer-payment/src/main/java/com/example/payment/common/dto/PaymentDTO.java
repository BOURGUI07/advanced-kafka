package com.example.payment.common.dto;

import com.example.common.events.payment.PaymentStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record PaymentDTO(
        UUID paymentId,
        UUID orderId,
        Integer customerId,
        PaymentStatus paymentStatus,
        Integer amount
) {
}
