package com.example.payment.common.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(Integer customerId) {
        super("Insufficient balance for customer with id " + customerId);
    }
}
