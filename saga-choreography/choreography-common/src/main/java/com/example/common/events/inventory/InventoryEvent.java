package com.example.common.events.inventory;

import com.example.common.events.DomainEvent;
import com.example.common.events.OrderSaga;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

public sealed interface InventoryEvent extends OrderSaga, DomainEvent {

    @Builder
    record Deducted(UUID orderId,
                    UUID inventoryId,
                    Integer productId,
                    Integer quantity,
                    Instant createdAt) implements InventoryEvent {}

    @Builder
    record Restored(UUID orderId,
                    UUID inventoryId,
                    Integer productId,
                    Integer quantity,
                    Instant createdAt) implements InventoryEvent {}

    @Builder
    record Declined(UUID orderId,
                    Integer productId,
                    Integer quantity,
                    String message,
                    Instant createdAt) implements InventoryEvent {}
}
