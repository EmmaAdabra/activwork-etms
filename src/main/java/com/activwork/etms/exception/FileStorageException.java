package com.activwork.etms.exception;

/**
 * Exception thrown when file storage operations fail.
 * Extends RuntimeException for Spring transaction management.
 */
public class FileStorageException extends RuntimeException {
    
    public FileStorageException(String message) {
        super(message);
    }
    
    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}

