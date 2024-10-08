package com.example.order.messaging.publisher;

import com.example.common.events.order.OrderEvent;
import com.example.common.publisher.EventPublisher;
import com.example.order.common.dto.PurchaseOrderDTO;
import com.example.order.common.service.OrderEventListener;
import com.example.order.messaging.mapper.OrderEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;

@RequiredArgsConstructor
public class OrderEventListenerEventImpl implements OrderEventListener, EventPublisher<OrderEvent> {
    private final Flux<OrderEvent> orderEventFlux;
    private final Sinks.Many<OrderEvent> sink;

    @Override
    public void emitCreatedOrder(PurchaseOrderDTO purchaseOrderDTO) {
        var event = OrderEventMapper.toOrderCreatedEvent(purchaseOrderDTO);
        sink.emitNext(event, Sinks.EmitFailureHandler.busyLooping(Duration.ofSeconds(1)));
    }

    @Override
    public Flux<OrderEvent> publish() {
        return orderEventFlux;
    }
}
