package com.app.whatsApp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class HandelException {
    @ExceptionHandler
    public ResponseEntity<AppResponse> handelException(RuntimeException exc) {
        AppResponse error = AppResponse.builder()
                .message(exc.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(new Date())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler
    public ResponseEntity<AppResponse> handelException(Exception exc) {
        AppResponse error = AppResponse.builder()
                .message(exc.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(new Date())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


}
