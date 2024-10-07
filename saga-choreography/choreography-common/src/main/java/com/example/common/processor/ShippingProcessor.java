package com.example.common.processor;

import com.example.common.events.DomainEvent;
import com.example.common.events.shipping.ShippingEvent;
import reactor.core.publisher.Mono;

public interface ShippingProcessor<R extends DomainEvent> extends EventProcessor<ShippingEvent, R> {
    @Override
    default Mono<R> process(ShippingEvent event) {
        return switch(event){
            case ShippingEvent.Scheduled e-> handle(e);   
        };
    }

    Mono<R> handle(ShippingEvent.Scheduled e);
}
