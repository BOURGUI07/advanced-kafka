package com.example.inventory.common.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record InventoryProcessRequest(
        UUID orderId,
        Integer quantity,
        Integer productId
) {
}
