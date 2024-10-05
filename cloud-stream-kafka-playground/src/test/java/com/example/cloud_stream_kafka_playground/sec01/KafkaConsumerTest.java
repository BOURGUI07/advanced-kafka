package com.example.cloud_stream_kafka_playground.sec01;


import com.example.cloud_stream_kafka_playground.AbstractIntegrationTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.function.Supplier;

@ExtendWith(OutputCaptureExtension.class)
@TestPropertySource(properties = {
        "sec=sec01",
        "spring.cloud.function.definition=consumer;testProducer",
        "spring.cloud.stream.bindings.testProducer-out-0.destination=input-topic"
})
public class KafkaConsumerTest extends AbstractIntegrationTests {
    @TestConfiguration
    static class testConfig{
        @Bean
        public Supplier<Flux<String>> testProducer(){
            return () -> Flux.just("Hello World");
        }
    }


    @Test
    void consumeTest(CapturedOutput output){
        Mono.delay(Duration.ofMillis(500))
                .then(Mono.fromSupplier(output::getOut))
                .as(StepVerifier::create)
                .consumeNextWith(s-> Assertions.assertTrue(s.contains("CONSUMER RECEIVED: Hello World")))
                .verifyComplete();


    }

}
