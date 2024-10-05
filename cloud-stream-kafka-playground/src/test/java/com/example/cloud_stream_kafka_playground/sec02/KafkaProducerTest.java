package com.example.cloud_stream_kafka_playground.sec02;

import com.example.cloud_stream_kafka_playground.AbstractIntegrationTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.function.Consumer;
@TestPropertySource(properties = {
        "sec=sec02",
        "spring.cloud.function.definition=producer;testConsumer",
        "spring.cloud.stream.bindings.testConsumer-in-0.destination=input-topic"
})
public class KafkaProducerTest extends AbstractIntegrationTests {
    private static final Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();


    @TestConfiguration
    static class testConfig{
        @Bean
        public Consumer<Flux<String>> testConsumer(){
            return flux -> flux
                    .doOnNext(sink::tryEmitNext)
                    .subscribe();
        }
    }

    @Test
    void producerTest(){
        sink.asFlux()
                .take(2)
                .timeout(Duration.ofSeconds(20))
                .as(StepVerifier::create)
                .consumeNextWith(x-> Assertions.assertEquals("Message-0", x))
                .consumeNextWith(x-> Assertions.assertEquals("Message-1", x))
                .verifyComplete();
    }

}
