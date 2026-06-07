package com.firstclub.interview.controller;

import com.firstclub.interview.dto.ApiErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    ResponseEntity<?> handleOptimisticLock() {
        return ResponseEntity.status(409).body(ApiErrorResponse.of(409, "Membership was modified concurrently. Please retry."));
    }

    @ExceptionHandler(IllegalStateException.class)
    ResponseEntity<?> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(409).body(ApiErrorResponse.of(409, ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(404).body(ApiErrorResponse.of(404, ex.getMessage()));
    }
}
