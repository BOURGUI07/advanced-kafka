package com.example.common.util;

import com.example.common.exception.EventAlreadyProcessedException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Slf4j
public class DuplicateEventValidator {
    public static Function<Mono<Boolean>, Mono<Void>> emitErrorForRedundantProcessing(){
        return mono ->mono
                .flatMap(b-> b? Mono.error(EventAlreadyProcessedException::new):Mono.empty())
                .doOnError(EventAlreadyProcessedException.class,ex->log.warn("DUPLICATE EVENT"))
                .then();
    }

    public static <T> Mono<T> validate(Mono<Boolean> eventValidationPublisher, Mono<T> eventProcessingPublisher){
        return eventValidationPublisher
                .transform(emitErrorForRedundantProcessing())
                .then(eventProcessingPublisher);
    }
}
