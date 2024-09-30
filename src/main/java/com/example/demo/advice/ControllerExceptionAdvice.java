package com.example.demo.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ControllerExceptionAdvice {

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException e) {
    ErrorResponse errorResponse = new ErrorResponse(e.getStatusCode().toString(), e.getReason());
    return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
  }

  public record ErrorResponse(String statusCode, String message) {

  }
}
