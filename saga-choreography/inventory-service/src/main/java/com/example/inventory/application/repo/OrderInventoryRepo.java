package com.example.inventory.application.repo;

import com.example.common.events.inventory.InventoryStatus;
import com.example.common.events.order.OrderStatus;
import com.example.inventory.application.entity.OrderInventory;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface OrderInventoryRepo extends ReactiveCrudRepository<OrderInventory, UUID> {
    Mono<Boolean> existsByOrderId(UUID orderId);
    Mono<OrderInventory> findByOrderIdAndStatus(UUID orderId, InventoryStatus status);
}
