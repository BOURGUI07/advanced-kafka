package com.example.cloud_stream_kafka_playground.sec04;

import com.example.cloud_stream_kafka_playground.AbstractIntegrationTests;
import com.example.cloud_stream_kafka_playground.common.MessageConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.function.Consumer;

@TestPropertySource(properties = {
        "sec=sec04",
        "spring.cloud.function.definition=producer;testConsumer",
        "spring.cloud.stream.bindings.testConsumer-in-0.destination=input-topic"
})
public class KafkaProducerWithKeyTest extends AbstractIntegrationTests {
    private static final Sinks.Many<Message<String>> sink = Sinks.many().unicast().onBackpressureBuffer();


    @TestConfiguration
    static class testConfig{
        @Bean
        public Consumer<Flux<Message<String>>> testConsumer(){
            return flux -> flux
                    .doOnNext(sink::tryEmitNext)
                    .subscribe();
        }
    }

    @Test
    void producerTest(){
        sink.asFlux()
                .map(MessageConverter::toRecord)
                .take(1)
                .timeout(Duration.ofSeconds(5))
                .as(StepVerifier::create)
                .consumeNextWith(r-> {
                    Assertions.assertEquals("Message-0",r.message());
                    Assertions.assertEquals("key-0",r.key());
                })
                .verifyComplete();
    }

}
