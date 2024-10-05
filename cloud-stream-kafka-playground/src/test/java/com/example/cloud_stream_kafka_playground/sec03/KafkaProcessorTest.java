package com.example.cloud_stream_kafka_playground.sec03;

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
import java.util.function.Supplier;

@Slf4j
@TestPropertySource(properties = {
        "sec=sec03",
        "spring.cloud.function.definition=testProducer;testConsumer;processor",
        "spring.cloud.stream.bindings.testProducer-out-0.destination=input-topic",
        "spring.cloud.stream.bindings.testConsumer-in-0.destination=output-topic"
})
public class KafkaProcessorTest extends AbstractIntegrationTests {
    private static final Sinks.Many<String> requestSink = Sinks.many().unicast().onBackpressureBuffer();
    private static final Sinks.Many<String> responseSink = Sinks.many().unicast().onBackpressureBuffer();


    @TestConfiguration
    static class testConfig{
        @Bean
        public Consumer<Flux<String>> testConsumer(){
            return flux -> flux
                    .doOnNext(responseSink::tryEmitNext)
                    .subscribe();
        }

        @Bean
        public Supplier<Flux<String>> testProducer(){
            return requestSink::asFlux;
        }
    }

    @Test
    void processorTest(){
        requestSink.tryEmitNext("sam");
        requestSink.tryEmitNext("mike");



        responseSink.asFlux()
                .take(2)
                .timeout(Duration.ofSeconds(5))
                .doOnNext(x->log.info("TEST RECEIVED: {}",x))
                .as(StepVerifier::create)
                .consumeNextWith(m->Assertions.assertEquals("SAM",m))
                .consumeNextWith(m->Assertions.assertEquals("MIKE",m))
                .verifyComplete();
    }
}
