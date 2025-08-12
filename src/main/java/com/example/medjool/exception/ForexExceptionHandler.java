package com.example.medjool.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ForexExceptionHandler {

    @ExceptionHandler(ForexNotFoundException.class)
    public ResponseEntity<Object> handleForexNotFoundException(ForexNotFoundException e) {
        String message = "FOREX RATE NOT FOUND";
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }
}
