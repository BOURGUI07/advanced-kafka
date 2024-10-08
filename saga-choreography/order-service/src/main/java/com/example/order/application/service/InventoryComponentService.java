package com.example.order.application.service;

import com.example.order.application.entity.OrderInventory;
import com.example.order.application.mapper.OrderMapper;
import com.example.order.application.repo.OrderInventoryRepo;
import com.example.order.common.dto.OrderInventoryDTO;
import com.example.order.common.service.inventory.InventoryComponentFetcher;
import com.example.order.common.service.inventory.InventoryComponentStatusListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InventoryComponentService implements InventoryComponentFetcher, InventoryComponentStatusListener {
    private final OrderInventoryRepo repo;
    private final OrderMapper mapper;
    private static final OrderInventoryDTO DEFAULT_PAYMENT = OrderInventoryDTO.builder().build();

    @Override
    public Mono<OrderInventoryDTO> getComponent(UUID orderId) {
        return repo.findByOrderId(orderId)
                .map(mapper::toOrderInventoryDTO)
                .defaultIfEmpty(DEFAULT_PAYMENT);
    }

    @Override
    public Mono<Void> onSuccess(OrderInventoryDTO message) {
        return repo.findByOrderId(message.orderId())
                .switchIfEmpty(Mono.defer(() -> add(message,true)))
                .then();
    }

    @Override
    public Mono<Void> onFailure(OrderInventoryDTO message) {
        return repo.findByOrderId(message.orderId())
                .switchIfEmpty(Mono.defer(() -> add(message,false)))
                .then();
    }

    @Override
    public Mono<Void> onRollBack(OrderInventoryDTO message) {
        return repo.findByOrderId(message.orderId())
                .doOnNext(p->p.setStatus(message.status()))
                .flatMap(repo::save)
                .then();
    }

    private Mono<OrderInventory> add(OrderInventoryDTO message, Boolean success) {
        return repo.save(mapper.toOrderInventory(message).setSuccess(success));
    }
}
