package com.example.cloud_stream_kafka_playground.sec03;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.function.Supplier;

@Slf4j
@Configuration
public class KafkaProducer {

    @Bean
    public Supplier<Flux<String>> producer() {
        return () -> Flux.interval(Duration.ofSeconds(1))
                .take(10)
                .map(i -> "Message-" + i)
                .doOnNext(i->log.info("PRODUCING MESSAGE: {}",i));
    }
}
