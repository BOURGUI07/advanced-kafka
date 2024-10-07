package com.example.shipping.application.repo;

import com.example.common.events.shipping.ShippingStatus;
import com.example.shipping.application.entity.Shipment;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ShipmentRepo extends ReactiveCrudRepository<Shipment, UUID> {
    Mono<Boolean> existsByOrderId(UUID orderId);

    Mono<Shipment> findByOrderIdAndStatus(UUID orderId, ShippingStatus status);

    Mono<Void> deleteByOrderId(UUID orderId);
}
