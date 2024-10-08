package com.example.order.common.dto;

import com.example.common.events.inventory.InventoryStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderInventoryDTO(
        UUID orderId,
        UUID inventoryId,
        String message,
        InventoryStatus status
) {
}
