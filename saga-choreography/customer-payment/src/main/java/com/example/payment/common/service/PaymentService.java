package com.example.payment.common.service;

import com.example.payment.common.dto.PaymentDTO;
import com.example.payment.common.dto.PaymentProcessRequest;

import reactor.core.publisher.Mono;

import java.util.UUID;


public interface PaymentService {
    Mono<PaymentDTO> process(PaymentProcessRequest request);
    Mono<PaymentDTO> refund(UUID orderId);

}
