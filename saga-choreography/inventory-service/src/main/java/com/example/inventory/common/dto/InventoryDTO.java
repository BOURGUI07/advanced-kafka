package com.example.inventory.common.dto;

import com.example.common.events.inventory.InventoryStatus;
import com.example.common.events.order.OrderStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record InventoryDTO(
        UUID inventoryId,
        UUID orderId,
        Integer productId,
        Integer quantity,
        InventoryStatus orderStatus
) {
}
