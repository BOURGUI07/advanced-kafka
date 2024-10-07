package com.example.common.events.shipping;

import com.example.common.events.DomainEvent;
import com.example.common.events.OrderSaga;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

public sealed interface ShippingEvent extends DomainEvent, OrderSaga {

    @Builder
    record Scheduled(UUID orderId,
                     UUID shippingId,
                     Instant expectedDelivery,
                     Instant createdAt) implements ShippingEvent {}
}
