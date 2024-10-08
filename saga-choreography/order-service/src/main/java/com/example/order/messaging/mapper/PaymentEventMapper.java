package com.example.order.messaging.mapper;

import com.example.common.events.payment.PaymentEvent;
import com.example.common.events.payment.PaymentStatus;
import com.example.order.common.dto.OrderPaymentDTO;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventMapper {
    public OrderPaymentDTO toRefundedDTO(PaymentEvent.Refunded event) {
        return OrderPaymentDTO.builder()
                .orderId(event.orderId())
                .paymentId(event.paymentId())
                .status(PaymentStatus.REFUNDED)
                .build();
    }

    public OrderPaymentDTO toDeclinedDTO(PaymentEvent.Declined event) {
        return OrderPaymentDTO.builder()
                .orderId(event.orderId())
                .status(PaymentStatus.DECLINED)
                .build();
    }

    public OrderPaymentDTO toDeductedDTO(PaymentEvent.Deducted event) {
        return OrderPaymentDTO.builder()
                .orderId(event.orderId())
                .paymentId(event.paymentId())
                .status(PaymentStatus.DEDUCTED)
                .build();
    }
}
