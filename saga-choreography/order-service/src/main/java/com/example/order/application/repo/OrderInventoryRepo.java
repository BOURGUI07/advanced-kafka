package com.example.order.application.repo;

import com.example.order.application.entity.OrderInventory;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface OrderInventoryRepo extends ReactiveCrudRepository<OrderInventory, Integer> {
    Mono<OrderInventory> findByOrderId(UUID orderId);
}
