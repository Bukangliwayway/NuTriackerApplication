package com.example.NuTriacker.exception;

import com.example.NuTriacker.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(NOT_FOUND)
                .body(new ApiResponse(ex.getMessage(), null));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity
                .status(NOT_FOUND)
                .body(new ApiResponse(ex.getMessage(), null));
    }
}