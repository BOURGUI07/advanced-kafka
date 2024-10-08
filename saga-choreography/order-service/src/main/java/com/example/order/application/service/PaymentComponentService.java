package com.example.order.application.service;

import com.example.order.application.entity.OrderPayment;
import com.example.order.application.mapper.OrderMapper;
import com.example.order.application.repo.OrderPaymentRepo;
import com.example.order.common.dto.OrderPaymentDTO;
import com.example.order.common.service.payment.PaymentComponentFetcher;
import com.example.order.common.service.payment.PaymentComponentStatusListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentComponentService implements PaymentComponentStatusListener, PaymentComponentFetcher {
    private final OrderPaymentRepo repo;
    private final OrderMapper mapper;
    private static final OrderPaymentDTO DEFAULT_PAYMENT = OrderPaymentDTO.builder().build();

    @Override
    public Mono<OrderPaymentDTO> getComponent(UUID orderId) {
        return repo.findByOrderId(orderId)
                .map(mapper::toOrderPaymentDTO)
                .defaultIfEmpty(DEFAULT_PAYMENT);
    }

    @Override
    public Mono<Void> onSuccess(OrderPaymentDTO message) {
        return repo.findByOrderId(message.orderId())
                .switchIfEmpty(Mono.defer(() -> add(message,true)))
                .then();
    }

    @Override
    public Mono<Void> onFailure(OrderPaymentDTO message) {
        return repo.findByOrderId(message.orderId())
                .switchIfEmpty(Mono.defer(() -> add(message,false)))
                .then();
    }

    @Override
    public Mono<Void> onRollBack(OrderPaymentDTO message) {
        return repo.findByOrderId(message.orderId())
                .doOnNext(p->p.setStatus(message.status()))
                .flatMap(repo::save)
                .then();
    }

    private Mono<OrderPayment> add(OrderPaymentDTO message, Boolean success) {
        return repo.save(mapper.toOrderPayment(message).setSuccess(success));
    }
}
