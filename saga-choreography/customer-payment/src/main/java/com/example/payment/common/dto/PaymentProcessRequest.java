package com.example.payment.common.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PaymentProcessRequest(
        UUID orderId,
        Integer customerId,
        Integer amount
) {
}
