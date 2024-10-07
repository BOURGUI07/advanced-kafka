package com.example.inventory.common.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Integer productId) {
        super("Product with id " + productId + " not found");
    }
}
