package com.activwork.etms.exception;

/**
 * Exception thrown when a business rule is violated.
 * Typically results in HTTP 422 (Unprocessable Entity) response.
 * 
 * Examples:
 * - Attempting to enroll in a full course
 * - Trying to publish a course without required materials
 * - Enrolling without meeting prerequisites
 * 
 * This is a category exception - specific business rule exceptions should extend this class.
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
     * @param cause the underlying cause
     */
    public BusinessRuleViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}

