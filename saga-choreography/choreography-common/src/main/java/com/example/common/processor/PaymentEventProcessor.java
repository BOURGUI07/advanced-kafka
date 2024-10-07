package com.example.common.processor;

import com.example.common.events.DomainEvent;
import com.example.common.events.payment.PaymentEvent;
import reactor.core.publisher.Mono;

public interface PaymentEventProcessor<R extends DomainEvent> extends EventProcessor<PaymentEvent,R> {
    @Override
    default Mono<R> process(PaymentEvent event) {
        return switch(event){
            case PaymentEvent.Deducted e -> handle(e);
            case PaymentEvent.Refunded e -> handle(e);
            case PaymentEvent.Declined e -> handle(e);
        };
    }

    Mono<R> handle(PaymentEvent.Declined e);

     Mono<R> handle(PaymentEvent.Refunded e);

    Mono<R> handle(PaymentEvent.Deducted e);
}
