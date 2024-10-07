package com.example.inventory.common.exception;

public class OutOfStockException extends RuntimeException {
    public OutOfStockException(Integer productId) {
        super("Product " + productId + " is out of stock");
    }
}
