package com.example.order.application.repo;

import com.example.order.application.entity.OrderPayment;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface OrderPaymentRepo extends ReactiveCrudRepository<OrderPayment, Integer> {
    Mono<OrderPayment> findByOrderId(UUID orderId);
}
