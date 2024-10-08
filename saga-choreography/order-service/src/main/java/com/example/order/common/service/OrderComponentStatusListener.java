package com.example.order.common.service;

import reactor.core.publisher.Mono;

public interface OrderComponentStatusListener<T> {
    Mono<Void> onSuccess(T message);
    Mono<Void> onFailure(T message);
    Mono<Void> onRollBack(T message);
}
