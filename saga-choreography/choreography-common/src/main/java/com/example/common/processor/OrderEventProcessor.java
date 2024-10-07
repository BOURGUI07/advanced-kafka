package com.example.common.processor;

import com.example.common.events.DomainEvent;
import com.example.common.events.order.OrderEvent;
import reactor.core.publisher.Mono;

public interface OrderEventProcessor<R extends DomainEvent> extends EventProcessor<OrderEvent,R> {
    @Override
    default Mono<R> process(OrderEvent event){
        return switch (event){
            case OrderEvent.Created e -> handle(e);
            case OrderEvent.Cancelled e -> handle(e);
            case OrderEvent.Completed e -> handle(e);
        };
    }

    Mono<R>  handle(OrderEvent.Completed e);

    Mono<R>  handle(OrderEvent.Cancelled e);

    Mono<R>  handle(OrderEvent.Created e);
}
