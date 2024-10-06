package com.example.cloud_stream_kafka_playground.sec05_1.consumer;

import com.example.cloud_stream_kafka_playground.common.MessageConverter;
import com.example.cloud_stream_kafka_playground.common.Record;
import com.example.cloud_stream_kafka_playground.sec05_1.dto.PhysicalDelivery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Configuration
@Slf4j
public class PhysicalDeliveryConsumer {

    @Bean
    public Function<Flux<Message<PhysicalDelivery>>, Mono<Void>> physicalConsumer() {
        return flux -> flux
                .map(MessageConverter::toRecord)
                .doOnNext(this::printDetails)
                .then();
    }

    private void printDetails(Record<PhysicalDelivery> record) {
        log.info("Received physical delivery: {}", record.message());
        record.acknowledgement().acknowledge();
    }
}
