package com.example.medjool.exception;

public class ClientNotActiveException extends RuntimeException {
  public ClientNotActiveException(String message) {
    super(message);
  }
}
