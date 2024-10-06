package com.example.cloud_stream_kafka_playground.sec08.consumer;

import com.example.cloud_stream_kafka_playground.common.MessageConverter;
import com.example.cloud_stream_kafka_playground.common.Record;
import com.example.cloud_stream_kafka_playground.sec08.dto.DigitalDelivery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Configuration
@Slf4j
public class DigitalDeliveryConsumer {

    @Bean
    public Function<Flux<Message<DigitalDelivery>>, Mono<Void>> digitalConsumer() {
        return flux -> flux
                .map(MessageConverter::toRecord)
                .doOnNext(this::printDetails)
                .then();
    }

    private void printDetails(Record<DigitalDelivery> record) {
        log.info("Received digital delivery: {}", record.message());
        record.acknowledgement().acknowledge();
    }
}
