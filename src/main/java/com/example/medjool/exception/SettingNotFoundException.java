package com.example.medjool.exception;

public class SettingNotFoundException extends RuntimeException {
  public SettingNotFoundException(String message) {
    super(message);
  }
}
