package com.example.cloud_stream_kafka_playground.sec07.processor;

import com.example.cloud_stream_kafka_playground.common.MessageConverter;
import com.example.cloud_stream_kafka_playground.common.Record;
import com.example.cloud_stream_kafka_playground.sec07.dto.DigitalDelivery;
import com.example.cloud_stream_kafka_playground.sec07.dto.OrderEvent;
import com.example.cloud_stream_kafka_playground.sec07.dto.PhysicalDelivery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;

import java.util.function.Function;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class OrderRouter {
    private static final String DESTINATION_HEADER = "spring.cloud.stream.sendto.destination";
    private static final String DIGITAL_DELIVERY_CHANNEL = "digital-topic";
    private static final String PHYSICAL_DELIVERY_CHANNEL = "physical-topic";



    @Bean
    public Function<Flux<Message<OrderEvent>>, Flux<Message<?>>> processor(){
        return flux -> flux
                .map(MessageConverter::toRecord)
                .map(this::route);
    }

    private Message<?> route(Record<OrderEvent> record) {
        var orderEvent = record.message();
        var msg = switch(orderEvent.type()){
            case DIGITAL -> this.toDigitalDelivery(orderEvent);
            case PHYSICAL -> this.toPhysicalDelivery(orderEvent);
        };
        record.acknowledgement().acknowledge();
        return msg;
    }

    private Message<DigitalDelivery> toDigitalDelivery(OrderEvent orderEvent) {
        var digitalDelivery = new DigitalDelivery(orderEvent.productId(), orderEvent.customerId()+"@gmail.com");
        return MessageBuilder.withPayload(digitalDelivery)
                .setHeader(DESTINATION_HEADER,DIGITAL_DELIVERY_CHANNEL)
                .build();
    }

    private Message<PhysicalDelivery> toPhysicalDelivery(OrderEvent orderEvent) {
        var customerId = orderEvent.customerId();
        var physicalDelivery = new PhysicalDelivery(orderEvent.productId(), customerId+"Streat",customerId+"City",customerId+"Country");
        return MessageBuilder.withPayload(physicalDelivery)
                .setHeader(DESTINATION_HEADER,PHYSICAL_DELIVERY_CHANNEL)
                .build();
    }

}
