package com.zorvyn.finance.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception interceptor for the Zorvyn API.
 * This class catches exceptions thrown by any controller and transforms them into
 * standardized JSON error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation errors from @Valid annotations on RequestDTOs.
     * Extracts field names and specific constraint messages (e.g., "amount: must not be null").
     * @return A map of validation errors with a 400 Bad Request status.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildErrorResponse(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles cases where a requested database entity does not exist.
     * @return The exception message with a 404 Not Found status.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles Spring Security authorization failures.
     * @return A generic "Access denied" message with a 403 Forbidden status.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        return buildErrorResponse("Access denied", HttpStatus.FORBIDDEN);
    }

    /**
     * Handles {@link IllegalArgumentException}, typically thrown when a method
     * receives an argument that is inappropriate or out of range for the business logic.
     * <p>
     * <b>Example:</b> Attempting to register a user with an email that is already in use.
     * </p>
     *
     * @param ex The caught IllegalArgumentException.
     * @return A {@link ResponseEntity} containing the error message and a 400 Bad Request status.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles authentication failures, such as incorrect passwords.
     * @return A masked error message with a 401 Unauthorized status.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        return buildErrorResponse("Invalid username or password", HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles cases where the incoming JSON request body is malformed or cannot
     * be deserialized into the target DTO.
     * <p>
     * This commonly occurs due to syntax errors in the JSON (e.g., missing commas,
     * mismatched brackets) or providing a string where a number is expected.
     * </p>
     *
     * @param ex The caught HttpMessageNotReadableException.
     * @return A {@link ResponseEntity} with a "Malformed JSON request" message and 400 Bad Request status.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return buildErrorResponse("Malformed JSON request", HttpStatus.BAD_REQUEST);
    }

    /**
     * Final fallback for any unhandled system exceptions.
     * Prevents internal stack traces from leaking to the client.
     * @return A generic error message with a 500 Internal Server Error status.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return buildErrorResponse("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Helper method to construct a consistent JSON error structure for all exceptions.
     * <p>
     * By using this centralized builder, the Zorvyn API ensures that every error
     * response contains a timestamp, HTTP status code, and a descriptive message,
     * making it easier for frontend clients to display meaningful feedback.
     * </p>
     *
     * @param message The specific error description to show the user.
     * @param status  The HTTP status code appropriate for the error.
     * @return A {@link ResponseEntity} wrapping the error details map.
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", message);
        body.put("status", status.value());
        return new ResponseEntity<>(body, status);
    }
}
