package com.example.cloud_stream_kafka_playground.sec03;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

@Slf4j
@Configuration
public class KafkaConsumer {
    @Bean
    public Consumer<Flux<String>> consumer(){
        return  flux -> flux
                .doOnNext(x->log.info("CONSUMER RECEIVED: {}",x))
                .subscribe();
    }

}
