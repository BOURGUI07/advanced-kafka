package com.example.common.publisher;

import com.example.common.events.DomainEvent;
import reactor.core.publisher.Flux;

public interface EventPublisher<T extends DomainEvent> {
    Flux<T> publish();
}
