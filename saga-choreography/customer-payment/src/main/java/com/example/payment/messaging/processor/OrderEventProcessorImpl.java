package com.example.payment.messaging.processor;

import com.example.common.events.order.OrderEvent;
import com.example.common.events.payment.PaymentEvent;
import com.example.common.exception.EventAlreadyProcessedException;
import com.example.common.processor.OrderEventProcessor;
import com.example.payment.common.service.PaymentService;
import com.example.payment.messaging.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.function.UnaryOperator;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProcessorImpl implements OrderEventProcessor<PaymentEvent> {

    private final PaymentService service;
    private final MessageMapper mapper;

    @Override
    public Mono<PaymentEvent> handle(OrderEvent.Created e) {
        return service.process(mapper.toPaymentRequest(e))
                .map(mapper::toDeductedPaymentEvent)
                .doOnNext(event->log.info("PAYMENT PROCESSED: {}", event))
                .transform(exceptionHandler(e));

    }

    private UnaryOperator<Mono<PaymentEvent>> exceptionHandler(OrderEvent.Created event) {
        return mono -> mono
                .onErrorResume(EventAlreadyProcessedException.class,e->Mono.empty())
                .onErrorResume(mapper.toDeclinedPaymentEvent(event));

    }


    @Override
    public Mono<PaymentEvent> handle(OrderEvent.Completed e) {
        return Mono.empty();
    }

    @Override
    public Mono<PaymentEvent> handle(OrderEvent.Cancelled e) {
        return service.refund(e.orderId())
                .map(mapper::toRefundedPaymentEvent)
                .doOnNext(event->log.info("REFUND PROCESSED: {}", event))
                .doOnError(ex->log.info("ERROR WHILE PROCESSING REFUND: {}", ex.getMessage()));
    }
}
