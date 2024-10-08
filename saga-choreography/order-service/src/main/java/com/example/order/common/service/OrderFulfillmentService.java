package com.example.order.common.service;

import com.example.order.common.dto.PurchaseOrderDTO;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OrderFulfillmentService {
    Mono<PurchaseOrderDTO> complete(UUID orderId);
    Mono<PurchaseOrderDTO> cancel(UUID orderId);
}
