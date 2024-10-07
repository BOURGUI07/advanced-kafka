package com.example.payment.common.service;

import com.example.payment.application.mapper.PaymentMapper;
import com.example.payment.application.repo.CustomerPaymentRepo;
import com.example.payment.application.repo.CustomerRepo;
import com.example.payment.common.dto.PaymentDTO;
import com.example.payment.common.dto.PaymentProcessRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;


public interface PaymentService {
    Mono<PaymentDTO> process(PaymentProcessRequest request);
    Mono<PaymentDTO> refund(UUID orderId);

}
