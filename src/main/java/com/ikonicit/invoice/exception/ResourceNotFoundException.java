package com.ikonicit.invoice.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * If your project already has a shared ResourceNotFoundException
 * (common across Client/Invoice modules), delete this file and swap
 * the import in OrderServiceImpl to point at your existing one instead.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
