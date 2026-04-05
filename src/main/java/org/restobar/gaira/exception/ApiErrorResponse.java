package org.restobar.gaira.exception;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ApiErrorResponse(
        LocalDateTime timestamp,
        int statusCode,
        String message,
        Object errors,
        String path
) {
}
