package com.example.common.processor;

import com.example.common.events.DomainEvent;
import com.example.common.events.inventory.InventoryEvent;
import reactor.core.publisher.Mono;

import java.awt.event.InvocationEvent;

public interface InventoryEventProcessor<R extends DomainEvent> extends EventProcessor<InventoryEvent,R> {
    @Override
    default Mono<R> process(InventoryEvent event) {
        return switch(event){
            case InventoryEvent.Deducted  e-> handle(e);
            case InventoryEvent.Restored  e-> handle(e);
            case InventoryEvent.Declined  e-> handle(e);
        };
    }

    Mono<R> handle(InventoryEvent.Declined e);

    Mono<R> handle(InventoryEvent.Restored e);

    Mono<R> handle(InventoryEvent.Deducted e);
}
