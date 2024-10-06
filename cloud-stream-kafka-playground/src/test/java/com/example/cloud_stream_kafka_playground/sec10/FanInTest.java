package com.example.cloud_stream_kafka_playground.sec10;

import com.example.cloud_stream_kafka_playground.AbstractIntegrationTests;
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

@TestPropertySource(properties = {
        "sec=sec10",
        "spring.cloud.function.definition=processor;tempProducer;humidityProducer;heatIndexConsumer",
        "spring.cloud.stream.bindings.tempProducer-out-0.destination=temperature-topic",
        "spring.cloud.stream.bindings.humidityProducer-out-0.destination=humidity-topic",
        "spring.cloud.stream.bindings.processor-in-0.destination=temperature-topic",
        "spring.cloud.stream.bindings.processor-in-1.destination=humidity-topic",
        "spring.cloud.stream.bindings.processor-out-0.destination=heat-index-topic",
        "spring.cloud.stream.bindings.heatIndexConsumer-in-0.destination=heat-index-topic"
})
public class FanInTest extends AbstractIntegrationTests {
    private static final Sinks.Many<Integer> temperatureSink = Sinks.many().unicast().onBackpressureBuffer();
    private static final Sinks.Many<Integer> relativeHumiditySink = Sinks.many().unicast().onBackpressureBuffer();
    private static final Sinks.Many<Long> heatIndexSink = Sinks.many().unicast().onBackpressureBuffer();

    @Test
    public void fanInTest(){
        heatIndexSink.asFlux()
                .take(3)
                .timeout(Duration.ofSeconds(5))
                .as(StepVerifier::create)
                .then(() -> temperatureSink.tryEmitNext(90))
                .then(() -> relativeHumiditySink.tryEmitNext(55))
                .consumeNextWith(index -> Assertions.assertEquals(97, index))
                .then(() -> relativeHumiditySink.tryEmitNext(60))
                .consumeNextWith(index -> Assertions.assertEquals(100, index))
                .then(() -> temperatureSink.tryEmitNext(94))
                .consumeNextWith(index -> Assertions.assertEquals(110, index))
                .verifyComplete();
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public Supplier<Flux<Integer>> tempProducer(){
            return temperatureSink::asFlux;
        }

        @Bean
        public Supplier<Flux<Integer>> humidityProducer(){
            return relativeHumiditySink::asFlux;
        }

        @Bean
        public Consumer<Flux<Long>> heatIndexConsumer(){
            return f -> f.doOnNext(heatIndexSink::tryEmitNext).subscribe();
        }

    }
}
