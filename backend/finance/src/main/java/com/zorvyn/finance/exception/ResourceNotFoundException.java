package com.zorvyn.finance.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception thrown when a requested resource (User, FinancialRecord, Category, etc.)
 * cannot be found in the database.
 * <p>
 * Annotated with {@link org.springframework.web.bind.annotation.ResponseStatus} to
 * automatically return an {@code HTTP 404 NOT FOUND} status to the client when
 * this exception is propagated to the web layer.
 * </p>
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with a detailed error message.
     * @param message The specific details of the missing resource (e.g., "User not found with ID: 123").
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
