package com.example.cloud_stream_kafka_playground.sec03;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Slf4j
@Configuration
public class KafkaProcessor {

    @Bean
    public Function<Flux<String>, Flux<String>> processor() {
        return flux -> flux
                .doOnNext(x->log.info("PROCESSOR RECEIVED: {}",x))
                .concatMap(this::process)
                .doOnNext(x->log.info("PROCESSOR DONE: {}",x));


    }

    //service layer
    private Mono<String> process(String input) {
        return Mono.just(input) //DB call
                .map(String::toUpperCase);
    }
}
