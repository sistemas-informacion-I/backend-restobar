package org.restobar.gaira.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatusException(
            ResponseStatusException ex,
            HttpServletRequest request) {
        return build(ex.getStatusCode().value(), ex.getReason() != null ? ex.getReason() : ex.getMessage(),
                null, null, request);
    }

    @ExceptionHandler(LockoutException.class)
    public ResponseEntity<ApiErrorResponse> handleLockoutException(
            LockoutException ex,
            HttpServletRequest request) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("lockedUntil", ex.getLockedUntil());
        
        return build(HttpStatus.LOCKED.value(), ex.getMessage(), null, metadata, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN.value(), "No tienes permisos para realizar esta acción", null, null,
                request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return build(HttpStatus.BAD_REQUEST.value(), "Validación fallida", errors, null, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {
        List<String> errors = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();

        return build(HttpStatus.BAD_REQUEST.value(), "Validación de parámetros fallida", errors, null, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null, null, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralException(
            Exception ex,
            HttpServletRequest request) {
        logger.error("Excepción no manejada en " + request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error interno del servidor",
                null, null, request);
    }

    private ResponseEntity<ApiErrorResponse> build(int status, String message, Object errors,
            Map<String, Object> metadata, HttpServletRequest request) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .statusCode(status)
                .message(message)
                .errors(errors)
                .path(request.getRequestURI())
                .metadata(metadata)
                .build();

        return ResponseEntity.status(status).body(response);
    }
}
