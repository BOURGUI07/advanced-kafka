package com.example.shipping.common.service;

import com.example.shipping.common.dto.ScheduleRequest;
import com.example.shipping.common.dto.ShipmentDTO;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ShippingService {
    Mono<Void> addShipment(ScheduleRequest request);

    Mono<Void> cancel(UUID orderId);

    Mono<ShipmentDTO> schedule(UUID orderId);
}
