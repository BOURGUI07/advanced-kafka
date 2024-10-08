package com.example.order.messaging.processor;

import com.example.common.events.order.OrderEvent;
import com.example.common.events.payment.PaymentEvent;
import com.example.common.processor.PaymentEventProcessor;
import com.example.order.common.service.OrderFulfillmentService;
import com.example.order.common.service.payment.PaymentComponentStatusListener;
import com.example.order.messaging.mapper.OrderEventMapper;
import com.example.order.messaging.mapper.PaymentEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class PaymentEventProcessorImpl implements PaymentEventProcessor<OrderEvent> {
    private final PaymentComponentStatusListener listener;
    private final PaymentEventMapper mapper;
    private final OrderFulfillmentService service;

    @Override
    public Mono<OrderEvent> handle(PaymentEvent.Declined e) {
        var dto = mapper.toDeclinedDTO(e);
        return listener.onSuccess(dto)
                .then(service.cancel(e.orderId()))
                .map(OrderEventMapper::toOrderCancelledEvent);
    }

    @Override
    public Mono<OrderEvent> handle(PaymentEvent.Refunded e) {
        var dto = mapper.toRefundedDTO(e);
        return listener.onRollBack(dto)
                .then(Mono.empty());
    }

    @Override
    public Mono<OrderEvent> handle(PaymentEvent.Deducted e) {
        var dto = mapper.toDeductedDTO(e);
        return listener.onSuccess(dto)
                .then(service.complete(e.orderId()))
                .map(OrderEventMapper::toOrderCompletedEvent);
    }
}
