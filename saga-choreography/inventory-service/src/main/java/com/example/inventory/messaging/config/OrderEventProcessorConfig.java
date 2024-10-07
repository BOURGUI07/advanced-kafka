package com.example.inventory.messaging.config;

import com.example.common.events.inventory.InventoryEvent;
import com.example.common.events.order.OrderEvent;
import com.example.common.events.payment.PaymentEvent;
import com.example.common.processor.OrderEventProcessor;
import com.example.common.util.MessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;

import java.util.function.Function;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class OrderEventProcessorConfig {
    private final OrderEventProcessor<InventoryEvent> orderEventProcessor;

    @Bean
    public Function<Flux<Message<OrderEvent>>, Flux<Message<InventoryEvent>>> processor() {
        return flux -> flux.map(MessageConverter::toRecord)
                .doOnNext(r -> log.info("order inventory received {}", r.message()))
                .concatMap(r -> this.orderEventProcessor.process(r.message())
                        .doOnSuccess(e -> r.acknowledgement().acknowledge())
                )
                .map(this::toMessage);
    }

    private Message<InventoryEvent> toMessage(InventoryEvent event) {
        return MessageBuilder.withPayload(event)
                .setHeader(KafkaHeaders.KEY, event.orderId().toString())
                .build();
    }
}
