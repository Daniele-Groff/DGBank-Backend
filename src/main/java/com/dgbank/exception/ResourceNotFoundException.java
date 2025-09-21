package com.dgbank.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " non trovato con ID: " + id);
    }
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
