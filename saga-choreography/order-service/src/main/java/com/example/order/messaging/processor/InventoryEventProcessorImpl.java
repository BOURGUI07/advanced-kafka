package com.example.order.messaging.processor;

import com.example.common.events.inventory.InventoryEvent;
import com.example.common.events.order.OrderEvent;
import com.example.common.processor.InventoryEventProcessor;
import com.example.order.common.service.OrderFulfillmentService;
import com.example.order.common.service.inventory.InventoryComponentStatusListener;
import com.example.order.messaging.mapper.InventoryEventMapper;
import com.example.order.messaging.mapper.OrderEventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class InventoryEventProcessorImpl implements InventoryEventProcessor<OrderEvent> {
    private final InventoryComponentStatusListener listener;
    private final InventoryEventMapper mapper;
    private final OrderFulfillmentService service;

    @Override
    public Mono<OrderEvent> handle(InventoryEvent.Declined e) {
        var dto = mapper.toDeclinedDTO(e);
        return listener.onSuccess(dto)
                .then(service.cancel(e.orderId()))
                .map(OrderEventMapper::toOrderCancelledEvent);
    }

    @Override
    public Mono<OrderEvent> handle(InventoryEvent.Restored e) {
        var dto = mapper.toRestoredDTO(e);
        return listener.onRollBack(dto)
                .then(Mono.empty());
    }

    @Override
    public Mono<OrderEvent> handle(InventoryEvent.Deducted e) {
        var dto = mapper.toDeductedDTO(e);
        return listener.onSuccess(dto)
                .then(service.complete(e.orderId()))
                .map(OrderEventMapper::toOrderCompletedEvent);
    }
}
