package com.example.medjool.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ConfigurationExceptionController {

    @ExceptionHandler(PalletNotFoundException.class)
    public String handlePalletNotFoundException(PalletNotFoundException ex) {
        // Log the exception or perform any other necessary actions
        return "error/pallet-not-found"; // Return the view name for the error page
    }
}
