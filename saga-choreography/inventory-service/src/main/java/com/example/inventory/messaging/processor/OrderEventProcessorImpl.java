package com.example.inventory.messaging.processor;

import com.example.common.events.inventory.InventoryEvent;
import com.example.common.events.order.OrderEvent;
import com.example.common.exception.EventAlreadyProcessedException;
import com.example.common.processor.OrderEventProcessor;
import com.example.inventory.common.service.InventoryService;
import com.example.inventory.messaging.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.function.UnaryOperator;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProcessorImpl implements OrderEventProcessor<InventoryEvent> {

    private final InventoryService service;
    private final MessageMapper mapper;

    @Override
    public Mono<InventoryEvent> handle(OrderEvent.Created e) {
        return service.process(mapper.toInventoryRequest(e))
                .map(mapper::toDeductedInventoryEvent)
                .doOnNext(event->log.info("INVENTORY PROCESSED: {}", event))
                .transform(exceptionHandler(e));

    }

    private UnaryOperator<Mono<InventoryEvent>> exceptionHandler(OrderEvent.Created event) {
        return mono -> mono
                .onErrorResume(EventAlreadyProcessedException.class, e->Mono.empty())
                .onErrorResume(mapper.toDeclinedInventoryEvent(event));

    }


    @Override
    public Mono<InventoryEvent> handle(OrderEvent.Completed e) {
        return Mono.empty();
    }

    @Override
    public Mono<InventoryEvent> handle(OrderEvent.Cancelled e) {
        return service.restore(e.orderId())
                .map(mapper::toRestoredInventoryEvent)
                .doOnNext(event->log.info("INVENTORY RESTORATION PROCESSED: {}", event))
                .doOnError(ex->log.info("ERROR WHILE PROCESSING RESTORATION: {}", ex.getMessage()));
    }
}
