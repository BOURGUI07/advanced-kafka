package com.example.order.application.service;

import com.example.common.events.order.OrderStatus;
import com.example.order.application.mapper.OrderMapper;
import com.example.order.application.repo.PurchaseOrderRepo;
import com.example.order.common.dto.PurchaseOrderDTO;
import com.example.order.common.service.OrderFulfillmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderFulfillmentServiceImpl implements OrderFulfillmentService {
    private final PurchaseOrderRepo repo;
    private final OrderMapper mapper;
    @Override
    public Mono<PurchaseOrderDTO> complete(UUID orderId) {
        return repo.getWhenOrderComponentsCompleted(orderId)
                .doOnNext(o ->o.setStatus(OrderStatus.COMPLETED))
                .flatMap(repo::save)
                .map(mapper::toPurchaseOrderDTO);
    }

    @Override
    public Mono<PurchaseOrderDTO> cancel(UUID orderId) {
        return  repo.findByOrderIdAndStatus(orderId,OrderStatus.PENDING)
                .doOnNext(o ->o.setStatus(OrderStatus.CANCELLED))
                .flatMap(repo::save)
                .map(mapper::toPurchaseOrderDTO);
    }
}
