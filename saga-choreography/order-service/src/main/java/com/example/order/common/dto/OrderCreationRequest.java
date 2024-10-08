package com.example.order.common.dto;

import lombok.Builder;

@Builder
public record OrderCreationRequest(
        Integer productId,
        Integer customerId,
        Integer unitPrice,
        Integer quantity
) {
}
