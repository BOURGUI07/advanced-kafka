package com.example.cloud_stream_kafka_playground.sec05_1.producer;

import com.example.cloud_stream_kafka_playground.sec05_1.dto.OrderEvent;
import com.example.cloud_stream_kafka_playground.sec05_1.dto.OrderType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.function.Supplier;

@Configuration
@Slf4j
public class OrderEventProducerConfig {
    @Bean
    public Supplier<Flux<Message<OrderEvent>>> orderEventProducer() {
        return () -> Flux.range(1,10)
                .delayElements(Duration.ofSeconds(1))
                .map(this::toMessage)
                .doOnNext(m->log.info("PRODUCED: {}",m));
    }

    private Message<OrderEvent> toMessage(int i){
        var type = i%2==0? OrderType.DIGITAL: OrderType.PHYSICAL;
        var order = new OrderEvent(i,i,type);
        return MessageBuilder
                .withPayload(order)
                .setHeader(KafkaHeaders.KEY,"order-id-"+i)
                .build();
    }
}
