package com.example.order.messaging.config;

import com.example.common.events.DomainEvent;
import com.example.common.events.order.OrderEvent;
import com.example.common.util.MessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import com.example.common.processor.EventProcessor;

import java.util.function.Function;

@Slf4j
public class AbstractOrderEventRouterConfig {
    private static final String DESTINATION_HEADER = "spring.cloud.stream.sendto.destination";
    private static final String ORDER_EVENTS_CHANNEL = "order-events-channel";


    protected <T extends DomainEvent> Function<Flux<Message<T>>, Flux<Message<OrderEvent>>> processor(EventProcessor<T, OrderEvent> eventProcessor) {
        return flux -> flux.map(MessageConverter::toRecord)
                .doOnNext(r -> log.info("order service received {}", r.message()))
                .concatMap(r -> eventProcessor.process(r.message())
                        .doOnSuccess(e -> r.acknowledgement().acknowledge())
                )
                .map(this::toMessage);
    }

    protected Message<OrderEvent> toMessage(OrderEvent event) {
        log.info("order service produced {}", event);
        return MessageBuilder.withPayload(event)
                .setHeader(KafkaHeaders.KEY, event.orderId().toString())
                .setHeader(DESTINATION_HEADER, ORDER_EVENTS_CHANNEL)
                .build();
    }
}
