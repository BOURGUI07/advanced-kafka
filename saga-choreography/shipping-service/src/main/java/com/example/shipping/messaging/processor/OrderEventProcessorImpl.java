package com.example.shipping.messaging.processor;

import com.example.common.events.order.OrderEvent;
import com.example.common.events.shipping.ShippingEvent;
import com.example.common.exception.EventAlreadyProcessedException;
import com.example.common.processor.OrderEventProcessor;
import com.example.shipping.common.service.ShippingService;
import com.example.shipping.messaging.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.function.UnaryOperator;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProcessorImpl implements OrderEventProcessor<ShippingEvent> {

    private final ShippingService service;

    @Override
    public Mono<ShippingEvent> handle(OrderEvent.Created event) {
        return this.service.addShipment(MessageMapper.toScheduleRequest(event))
                .transform(exceptionHandler())
                .then(Mono.empty());
    }

    @Override
    public Mono<ShippingEvent> handle(OrderEvent.Cancelled event) {
        return this.service.cancel(event.orderId())
                .then(Mono.empty());
    }

    @Override
    public Mono<ShippingEvent> handle(OrderEvent.Completed event) {
        return this.service.schedule(event.orderId())
                .map(MessageMapper::toShippingScheduledEvent)
                .doOnNext(e -> log.info("shipping scheduled {}", e));
    }

    private <T> UnaryOperator<Mono<T>> exceptionHandler() {
        return mono -> mono.onErrorResume(EventAlreadyProcessedException.class, ex -> Mono.empty())
                .doOnError(ex -> log.error(ex.getMessage()));
    }

}
