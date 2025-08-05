package com.example.medjool.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class OrderExceptionController {


    @ExceptionHandler(OrderCannotBeCanceledException.class)
    public ResponseEntity<Object> handleOrderCannotBeCanceledException(OrderCannotBeCanceledException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Object> handleOrderNotFoundException(OrderNotFoundException ex) {
        return new ResponseEntity<>("Order not found... Please, select an appropriate order to move forward.", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OrderCannotBeChangedException.class)
    public ResponseEntity<Object> handleOrderChangeException(OrderCannotBeChangedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MixedOrderExceedPalletWeightException.class)
    public ResponseEntity<Object> handleMixedOrderExceedPalletWeightException(MixedOrderExceedPalletWeightException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


}
