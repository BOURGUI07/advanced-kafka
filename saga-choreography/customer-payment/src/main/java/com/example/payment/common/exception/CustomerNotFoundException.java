package com.example.payment.common.exception;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(Integer customerId) {
        super(String.format("Customer with id %s not found", customerId));
    }
}
