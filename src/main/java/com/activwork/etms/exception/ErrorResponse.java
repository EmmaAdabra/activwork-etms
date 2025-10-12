package com.activwork.etms.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standardized error response structure for API errors.
 * This DTO ensures all error responses follow a consistent format,
 * making it easier for frontend applications to handle errors.
 * 
 * Used by GlobalExceptionHandler to convert exceptions into HTTP responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * Timestamp when the error occurred
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * HTTP status code (e.g., 404, 400, 500)
     */
    private int status;

    /**
     * HTTP status reason phrase (e.g., "Not Found", "Bad Request")
     */
    private String error;

    /**
     * Detailed error message explaining what went wrong
     */
    private String message;

    /**
     * The request path that caused the error
     */
    private String path;

    /**
     * Convenience constructor that sets timestamp to current time
     */
    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}

