package com.example.cloud_stream_kafka_playground.sec12;

import com.example.cloud_stream_kafka_playground.AbstractIntegrationTests;
import com.example.cloud_stream_kafka_playground.sec11.dto.ContactMethod;
import com.example.cloud_stream_kafka_playground.sec11.dto.Email;
import com.example.cloud_stream_kafka_playground.sec11.dto.Phone;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Supplier;

@TestPropertySource(properties = {
        "sec=sec12",
        "spring.cloud.function.definition=consumer;producer1;producer2",
        "spring.cloud.stream.bindings.producer1-out-0.destination=input-topic1",
        "spring.cloud.stream.bindings.producer2-out-0.destination=input-topic2"
})
@ExtendWith(OutputCaptureExtension.class)
public class MultipleTopicConsumerTest extends AbstractIntegrationTests {
    private static final Sinks.Many<String> sink1 = Sinks.many().unicast().onBackpressureBuffer();
    private static final Sinks.Many<String> sink2 = Sinks.many().unicast().onBackpressureBuffer();

    @TestConfiguration
    static class TestConfig {

        @Bean
        public Supplier<Flux<String>> producer1(){
            return sink1::asFlux;
        }
        @Bean
        public Supplier<Flux<String>> producer2(){
            return sink2::asFlux;
        }
    }
    @Test
    void test(CapturedOutput output) throws InterruptedException {
        sink1.tryEmitNext("msg1");
        sink2.tryEmitNext("msg2");
        Thread.sleep(1000);
        Assertions.assertTrue(output.getOut().contains("msg1"));
        Assertions.assertTrue(output.getOut().contains("msg2"));
    }
}
