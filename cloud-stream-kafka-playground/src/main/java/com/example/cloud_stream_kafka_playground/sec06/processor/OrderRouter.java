package com.example.cloud_stream_kafka_playground.sec06.processor;

import com.example.cloud_stream_kafka_playground.common.MessageConverter;
import com.example.cloud_stream_kafka_playground.sec06.dto.DigitalDelivery;
import com.example.cloud_stream_kafka_playground.sec06.dto.OrderEvent;
import com.example.cloud_stream_kafka_playground.sec06.dto.PhysicalDelivery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class OrderRouter {
    private static final String DIGITAL_DELIVERY_CHANNEL = "digital-topic";
    private static final String PHYSICAL_DELIVERY_CHANNEL = "physical-topic";


    private final StreamBridge streamBridge;

    @Bean
    public Function<Flux<Message<OrderEvent>>, Mono<Void>> processor(){
        return flux -> flux
                .map(MessageConverter::toRecord)
                .doOnNext(r->this.route(r.message()))
                .doOnNext(r->r.acknowledgement().acknowledge())
                .then();
    }

    private void route(OrderEvent orderEvent) {
        switch(orderEvent.type()){
            case DIGITAL -> this.toDigitalDelivery(orderEvent);
            case PHYSICAL -> this.toPhysicalDelivery(orderEvent);
        }

    }

    private void toDigitalDelivery(OrderEvent orderEvent) {
        var digitalDelivery = new DigitalDelivery(orderEvent.productId(), orderEvent.customerId()+"@gmail.com");
        streamBridge.send(DIGITAL_DELIVERY_CHANNEL, digitalDelivery);
    }

    private void toPhysicalDelivery(OrderEvent orderEvent) {
        var customerId = orderEvent.customerId();
        var digitalDelivery = new PhysicalDelivery(orderEvent.productId(), customerId+"Streat",customerId+"City",customerId+"Country");
        streamBridge.send(PHYSICAL_DELIVERY_CHANNEL, digitalDelivery);
    }

}
