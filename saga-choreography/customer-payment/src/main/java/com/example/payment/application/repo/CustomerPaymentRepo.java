package com.example.payment.application.repo;

import com.example.common.events.payment.PaymentStatus;
import com.example.payment.application.entity.CustomerPayment;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface CustomerPaymentRepo extends ReactiveCrudRepository<CustomerPayment, UUID> {
    Mono<Boolean> existsByOrderId(UUID orderId);
    Mono<CustomerPayment> findByOrderIdAndStatus(UUID orderId, PaymentStatus status);
}
