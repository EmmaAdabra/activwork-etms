package com.activwork.etms.exception;

/**
 * Base exception class for all ETMS custom exceptions.
 * Provides a consistent foundation for exception handling across the application.
 * 
 * All domain-specific exceptions should extend this base class or one of its
 * category subclasses (ResourceNotFoundException, BusinessRuleViolationException, etc.).
 */
public abstract class EtmsException extends RuntimeException {

    /**
     * Constructs a new ETMS exception with the specified detail message.
     * 
     * @param message the detail message explaining the exception
     */
    public EtmsException(String message) {
        super(message);
    }

    /**
     * Constructs a new ETMS exception with the specified detail message and cause.
     * 
     * @param message the detail message explaining the exception
     * @param cause the cause of the exception
     */
    public EtmsException(String message, Throwable cause) {
        super(message, cause);
    }
}

