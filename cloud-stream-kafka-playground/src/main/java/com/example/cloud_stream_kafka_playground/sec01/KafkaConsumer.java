package com.example.cloud_stream_kafka_playground.sec01;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Configuration
public class KafkaConsumer {
    @Bean
    public Consumer<Flux<String>> consumer(){
        return  flux -> flux
                .doOnNext(x->log.info("CONSUMER RECEIVED: {}",x))
                .subscribe();

        /*
            Configure yaml file
            run kafka server
            docker exec -it kafka bash
            kafka-topics.sh --bootstrap-server localhost:9092 --topic input-topic --create
            kafka-console-producer.sh --bootstrap-server localhost:9092 --topic input-topic
            write messages
            start the application
            see the logs

         */
    }
    @Bean
    public Function<Flux<String>, Mono<Void>> function() {
        return flux -> flux
                .doOnNext(x -> log.info("FUNCTION RECEIVED: {}", x))
                .then();
    }

}
