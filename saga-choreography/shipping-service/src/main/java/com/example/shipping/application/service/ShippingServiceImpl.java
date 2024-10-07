package com.example.shipping.application.service;

import com.example.common.events.shipping.ShippingStatus;
import com.example.common.util.DuplicateEventValidator;
import com.example.shipping.application.entity.Shipment;
import com.example.shipping.application.mapper.ShipmentMapper;
import com.example.shipping.application.repo.ShipmentRepo;
import com.example.shipping.common.dto.ScheduleRequest;
import com.example.shipping.common.dto.ShipmentDTO;
import com.example.shipping.common.service.ShippingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingServiceImpl implements ShippingService {
    private final ShipmentRepo repo;
    private final ShipmentMapper mapper;

    @Override
    @Transactional
    public Mono<Void> addShipment(ScheduleRequest request) {
        return DuplicateEventValidator.validate(
                this.repo.existsByOrderId(request.orderId()),
                Mono.defer(() -> this.add(request))
        );
    }

    private Mono<Void> add(ScheduleRequest request) {
        var shipment = mapper.toEntity(request).setStatus(ShippingStatus.PENDING);
        return this.repo.save(shipment)
                .then();
    }

    @Override
    @Transactional
    public Mono<Void> cancel(UUID orderId) {
        return repo.deleteById(orderId);
    }

    @Override
    @Transactional
    public Mono<ShipmentDTO> schedule(UUID orderId) {
        return this.repo.findByOrderIdAndStatus(orderId, ShippingStatus.PENDING)
                .flatMap(this::schedule);
    }

    private Mono<ShipmentDTO> schedule(Shipment shipment) {
        shipment.setDeliveryDate(Instant.now().plus(Duration.ofDays(3)));
        shipment.setStatus(ShippingStatus.SCHEDULED);
        return this.repo.save(shipment)
                .map(mapper::toDto);
    }
}
