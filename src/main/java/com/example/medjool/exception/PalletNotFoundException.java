package com.example.medjool.exception;

public class PalletNotFoundException extends RuntimeException {
  public PalletNotFoundException(String message) {
    super(message);
  }
}
