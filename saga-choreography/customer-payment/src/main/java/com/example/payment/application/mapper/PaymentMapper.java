package com.example.payment.application.mapper;

import com.example.payment.application.entity.Customer;
import com.example.payment.application.entity.CustomerPayment;
import com.example.payment.common.dto.PaymentDTO;
import com.example.payment.common.dto.PaymentProcessRequest;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {
    public CustomerPayment toCustomerPayment(PaymentProcessRequest request) {
        return new CustomerPayment()
                .setCustomerId(request.customerId())
                .setAmount(request.amount())
                .setOrderId(request.orderId());
    }

    public PaymentDTO toPaymentDTO(CustomerPayment p) {
        return PaymentDTO.builder()
                .paymentStatus(p.getStatus())
                .amount(p.getAmount())
                .orderId(p.getOrderId())
                .customerId(p.getCustomerId())
                .paymentId(p.getPaymentId())
                .build();
    }
}
