package com.example.order.common.service;

import com.example.order.common.dto.OrderCreationRequest;
import com.example.order.common.dto.OrderDetails;
import com.example.order.common.dto.PurchaseOrderDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OrderService {
    Mono<PurchaseOrderDTO> placeOrder(OrderCreationRequest request);
    Flux<PurchaseOrderDTO> getAllOrders();
    Mono<OrderDetails> getOrderDetails(UUID orderId);
}
