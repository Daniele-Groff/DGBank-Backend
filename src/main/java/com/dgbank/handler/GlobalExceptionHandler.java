package com.dgbank.handler;

import com.dgbank.exception.*;
import com.dgbank.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    // Gestione errori 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<BaseResponse> handleResourceNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new BaseResponse(false, e.getMessage()));
    }
    
    // Gestione errori 400 di dati errati
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<BaseResponse> handleValidation(ValidationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new BaseResponse(false, e.getMessage()));
    }
    
    // Gestione errori 400 di logica di business
    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<BaseResponse> handleBusinessLogic(BusinessLogicException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new BaseResponse(false, e.getMessage()));
    }
    
    // Gestione errori 403
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<BaseResponse> handleUnauthorized(UnauthorizedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new BaseResponse(false, e.getMessage()));
    }
    
    // Gestione di tutti gli altri errori - 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> handleGeneral(Exception e) {
        System.err.println("Errore non gestito: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new BaseResponse(false, "Errore interno del server"));
    }
}