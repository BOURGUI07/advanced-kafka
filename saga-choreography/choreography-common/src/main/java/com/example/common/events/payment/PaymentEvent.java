package com.example.common.events.payment;

import com.example.common.events.DomainEvent;
import com.example.common.events.OrderSaga;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

public sealed interface PaymentEvent extends DomainEvent, OrderSaga {

    @Builder
    record Deducted(UUID orderId,
                    UUID paymentId,
                    Integer customerId,
                    Integer amount,
                    Instant createdAt) implements PaymentEvent {}

    @Builder
    record Refunded(UUID orderId,
                    UUID paymentId,
                    Integer customerId,
                    Integer amount,
                    Instant createdAt) implements PaymentEvent {}

    @Builder
    record Declined(UUID orderId,
                    Integer customerId,
                    Integer amount,
                    String message,
                    Instant createdAt) implements PaymentEvent {}
}
