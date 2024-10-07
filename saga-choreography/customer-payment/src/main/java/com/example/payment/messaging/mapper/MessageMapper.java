package com.example.payment.messaging.mapper;

import com.example.common.events.order.OrderEvent;
import com.example.common.events.payment.PaymentEvent;
import com.example.payment.common.dto.PaymentDTO;
import com.example.payment.common.dto.PaymentProcessRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.function.Function;

@Component
public class MessageMapper {

    public PaymentProcessRequest toPaymentRequest(OrderEvent.Created event) {
        return PaymentProcessRequest.builder()
                .amount(event.totalAmount())
                .customerId(event.customerId())
                .orderId(event.orderId())
                .build();
    }

    public PaymentEvent toDeductedPaymentEvent(PaymentDTO dto) {
        return PaymentEvent.Deducted.builder()
                .amount(dto.amount())
                .customerId(dto.customerId())
                .orderId(dto.orderId())
                .paymentId(dto.paymentId())
                .createdAt(Instant.now())
                .build();
    }

    public PaymentEvent toRefundedPaymentEvent(PaymentDTO dto) {
        return PaymentEvent.Refunded.builder()
                .amount(dto.amount())
                .customerId(dto.customerId())
                .orderId(dto.orderId())
                .paymentId(dto.paymentId())
                .createdAt(Instant.now())
                .build();
    }

    public Function<Throwable, Mono<PaymentEvent>> toDeclinedPaymentEvent(OrderEvent.Created event) {
        return ex -> Mono.fromSupplier(
                () -> PaymentEvent.Declined.builder()
                        .amount(event.totalAmount())
                        .customerId(event.customerId())
                        .orderId(event.orderId())
                        .createdAt(Instant.now())
                        .message(ex.getMessage())
                .build());
    }
}
