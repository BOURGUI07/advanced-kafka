package com.example.order.application.controller;

import com.example.order.common.dto.OrderCreationRequest;
import com.example.order.common.dto.OrderDetails;
import com.example.order.common.dto.PurchaseOrderDTO;
import com.example.order.common.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService service;

    @PostMapping
    public Mono<ResponseEntity<PurchaseOrderDTO>> placeOrder(
            @RequestBody Mono<OrderCreationRequest> request
    ){
        return request.flatMap(service::placeOrder)
                .map(ResponseEntity.accepted()::body);
    }

    @GetMapping
    public Flux<PurchaseOrderDTO> getAllOrders(){
        return service.getAllOrders();
    }

    @GetMapping("/{orderId}")
    public Mono<OrderDetails> getOrderDetails(@PathVariable UUID orderId){
        return service.getOrderDetails(orderId);
    }
}
