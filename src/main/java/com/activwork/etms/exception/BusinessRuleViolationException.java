package com.activwork.etms.exception;

/**
 * Exception thrown when a business rule is violated.
 * Typically results in HTTP 422 (Unprocessable Entity) response.
 * 
 * Use this exception when the request is valid but cannot be processed
 * due to business logic constraints.
 * 
 * Examples:
 * - Enrolling in a full course
 * - Publishing a course without required materials
 * - Submitting feedback without being enrolled
 */
public class BusinessRuleViolationException extends EtmsException {

    /**
     * Constructs a BusinessRuleViolationException with a custom message.
     * 
     * @param message the detail message explaining which business rule was violated
     */
    public BusinessRuleViolationException(String message) {
        super(message);
    }

    /**
     * Constructs a BusinessRuleViolationException with a message and cause.
     * 
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public BusinessRuleViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}
