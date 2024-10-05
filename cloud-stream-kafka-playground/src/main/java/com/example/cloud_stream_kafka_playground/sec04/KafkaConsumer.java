package com.example.cloud_stream_kafka_playground.sec04;

import com.example.cloud_stream_kafka_playground.common.MessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import com.example.cloud_stream_kafka_playground.common.Record;

import java.util.function.Consumer;

@Slf4j
@Configuration
public class KafkaConsumer {
    @Bean
    public Consumer<Flux<Message<String>>> consumer() {
        return flux -> flux
                .map(MessageConverter::toRecord)
                .doOnNext(this::printMessageDetails)
                .subscribe();
    }

    private void printMessageDetails(Record<String> record) {
        log.info("payload: {}", record.message());
        log.info("key: {}", record.key());
        record.acknowledgement().acknowledge();
    }

}
