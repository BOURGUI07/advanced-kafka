package com.example.payment.application.service;

import com.example.common.events.payment.PaymentStatus;
import com.example.common.util.DuplicateEventValidator;
import com.example.payment.application.entity.Customer;
import com.example.payment.application.entity.CustomerPayment;
import com.example.payment.application.mapper.PaymentMapper;
import com.example.payment.application.repo.CustomerPaymentRepo;
import com.example.payment.application.repo.CustomerRepo;
import com.example.payment.common.dto.PaymentDTO;
import com.example.payment.common.dto.PaymentProcessRequest;
import com.example.payment.common.exception.CustomerNotFoundException;
import com.example.payment.common.exception.InsufficientBalanceException;
import com.example.payment.common.service.PaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class PaymentServiceImpl implements PaymentService {
    CustomerRepo customerRepo;
    CustomerPaymentRepo repo;
    PaymentMapper mapper;


    @Override
    @Transactional
    public Mono<PaymentDTO> process(PaymentProcessRequest request) {
        var customerId = request.customerId();
        return DuplicateEventValidator.validate(
                repo.existsByOrderId(request.orderId()),
                customerRepo.findById(customerId)
        )
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(customerId)))
                .filter(c ->c.getBalance()>=request.amount())
                .switchIfEmpty(Mono.error(new InsufficientBalanceException(customerId)))
                .flatMap(c -> deduct(c,request))
                .doOnNext(x-> log.info("PAYMENT PROCESSED FOR ORDER: {}", request.orderId()));

    }

    private Mono<PaymentDTO> deduct(Customer customer, PaymentProcessRequest request) {
        var amount = request.amount();
        customer.setBalance(customer.getBalance()-amount);
        var payment = mapper.toCustomerPayment(request).setStatus(PaymentStatus.DEDUCTED);
        return customerRepo.save(customer)
                .then(repo.save(payment))
                .map(mapper::toPaymentDTO);
    }

    @Override
    @Transactional
    public Mono<PaymentDTO> refund(UUID orderId) {
      return repo.findByOrderIdAndStatus(orderId,PaymentStatus.DEDUCTED)
              .zipWhen(p->customerRepo.findById(p.getCustomerId()))
              .flatMap(x->processRefund(x.getT1(),x.getT2()))
              .doOnNext(x->log.info("PAYMENT REFUND PROCESSED FOR ORDER: {} WITH AMOUNT: {}", orderId,x.amount()));
    }

    private Mono<PaymentDTO> processRefund(CustomerPayment p, Customer c) {
        c.setBalance(c.getBalance()+p.getAmount());
        p.setStatus(PaymentStatus.REFUNDED);
        return customerRepo.save(c)
                .then(repo.save(p))
                .map(mapper::toPaymentDTO);
    }
}
