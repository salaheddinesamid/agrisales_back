package com.example.medjool.exception;

public class ProductLowStock extends RuntimeException {
    public ProductLowStock(String message) {
        super(message);
    }

    public ProductLowStock(String message, Throwable cause) {
        super(message, cause);
    }
}
