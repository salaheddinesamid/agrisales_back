package com.example.medjool.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ProductExceptionController {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Object> handleProductNotFoundException(ProductNotFoundException e) {
        String message = "PRODUCT " + "NOT FOUND";
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }



    @ExceptionHandler(ProductLowStock.class)
    public ResponseEntity<Object> handleProductLowStock(ProductLowStock e) {
        String message = "PRODUCT LOW STOCK";
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }
}
