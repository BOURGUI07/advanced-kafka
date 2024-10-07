package com.example.inventory.messaging.mapper;

import com.example.common.events.inventory.InventoryEvent;
import com.example.common.events.order.OrderEvent;
import com.example.inventory.common.dto.InventoryDTO;
import com.example.inventory.common.dto.InventoryProcessRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.function.Function;

@Component
public class MessageMapper {
    public InventoryProcessRequest toInventoryRequest(OrderEvent.Created event) {
        return InventoryProcessRequest.builder()
                .productId(event.productId())
                .quantity(event.quantity())
                .orderId(event.orderId())
                .build();
    }

    public InventoryEvent toDeductedInventoryEvent(InventoryDTO dto) {
        return InventoryEvent.Deducted.builder()
                .orderId(dto.orderId())
                .createdAt(Instant.now())
                .inventoryId(dto.inventoryId())
                .productId(dto.productId())
                .quantity(dto.quantity())
                .build();
    }

    public InventoryEvent toRestoredInventoryEvent(InventoryDTO dto) {
        return InventoryEvent.Restored.builder()
                .orderId(dto.orderId())
                .createdAt(Instant.now())
                .inventoryId(dto.inventoryId())
                .productId(dto.productId())
                .quantity(dto.quantity())
                .build();
    }

    public Function<Throwable, Mono<InventoryEvent>> toDeclinedInventoryEvent(OrderEvent.Created event) {
        return ex -> Mono.fromSupplier(
                () -> InventoryEvent.Declined.builder()
                        .orderId(event.orderId())
                        .createdAt(Instant.now())
                        .message(ex.getMessage())
                        .productId(event.productId())
                        .quantity(event.quantity())
                        .build());
    }
}
