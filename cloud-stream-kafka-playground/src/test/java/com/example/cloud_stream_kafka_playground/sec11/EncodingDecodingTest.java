package com.example.cloud_stream_kafka_playground.sec11;

import com.example.cloud_stream_kafka_playground.AbstractIntegrationTests;
import com.example.cloud_stream_kafka_playground.sec11.dto.ContactMethod;
import com.example.cloud_stream_kafka_playground.sec11.dto.Email;
import com.example.cloud_stream_kafka_playground.sec11.dto.Phone;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
@TestPropertySource(properties = {
        "sec=sec11",
        "spring.cloud.function.definition=consumer;contactMethodProducer",
        "spring.cloud.stream.bindings.contactMethodProducer-out-0.destination=input-topic",
        "spring.cloud.stream.bindings.contactMethodProducer-out-0.producer.useNativeEncoding=true",
        "spring.cloud.stream.kafka.bindings.contactMethodProducer-out-0.producer.configuration.value.serializer=org.springframework.kafka.support.serializer.JsonSerializer"
})
public class EncodingDecodingTest extends AbstractIntegrationTests {
    @Test
    void encodingDecoding() throws InterruptedException {
        /*
            var sender = this.<Integer,Integer>createSender(o->
                o.withKeySerializer(new IntegerSerializer())
                        .withValueSerializer(new IntegerSerializer())
        );

        Flux.range(1, 3)
                .map(i->this.toSenderRecord("input-topic",i,i))
                .as(sender::send)
                .doOnNext(sr->log.info("RESULT: {}",sr.correlationMetadata()))
                .subscribe();
         */
        Thread.sleep(1000);
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public Supplier<Flux<ContactMethod>> contactMethodProducer(){
            return () ->Flux.just(
                    new Email("youness@gmail.com"),
                    new Phone(12345)
            );
        }
    }
}
