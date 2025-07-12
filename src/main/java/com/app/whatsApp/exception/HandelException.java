package com.app.whatsApp.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class HandelException {

    @ExceptionHandler
    public ResponseEntity<AppErrorResponse> handelException(NoHandlerFoundException exc) {
        AppErrorResponse error = AppErrorResponse.builder()
                .time(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .message("API endpoint not found")
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<AppErrorResponse> handelException(RuntimeException exc) {
        AppErrorResponse error = AppErrorResponse.builder()
                .time(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .message(exc.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<AppErrorResponse> handelException(DataIntegrityViolationException exc) {


        AppErrorResponse error = AppErrorResponse.builder()
                .time(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .message(exc.getRootCause().getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<AppErrorResponse> handelException(ConstraintViolationException exc) {

        String errors = exc.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("|"));

        AppErrorResponse error = AppErrorResponse.builder()
                .time(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .message(errors)
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<AppErrorResponse> handelException(Exception exc) {
        AppErrorResponse error = AppErrorResponse.builder()
                .time(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message(exc.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


}