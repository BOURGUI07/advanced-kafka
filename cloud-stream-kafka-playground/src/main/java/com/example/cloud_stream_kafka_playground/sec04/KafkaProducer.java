package com.example.cloud_stream_kafka_playground.sec04;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.function.Supplier;

@Slf4j
@Configuration
public class KafkaProducer {

    @Bean
    public Supplier<Flux<Message<String>>> producer() {
        return () -> Flux.interval(Duration.ofSeconds(1))
                .take(10)
                .map(this::toMessage)
                .doOnNext(i->log.info("PRODUCING MESSAGE: {}",i));
    }

    private Message<String> toMessage(long i) {
        return MessageBuilder.withPayload("Message-" + i)
                .setHeader(KafkaHeaders.KEY, ("key-" + i))
                .setHeader("my-dummy-key", ("dummy-value-" + i))
                .build();
    }
}
