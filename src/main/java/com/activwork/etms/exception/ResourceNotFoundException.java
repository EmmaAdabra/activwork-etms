package com.activwork.etms.exception;

import java.util.UUID;

/**
 * Exception thrown when a requested resource cannot be found in the system.
 * Typically results in HTTP 404 (Not Found) response.
 * 
 * This is a category exception - specific resource exceptions should extend this class.
 */
public class ResourceNotFoundException extends EtmsException {

    /**
     * Constructs a ResourceNotFoundException with a custom message.
     * 
     * @param message the detail message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a ResourceNotFoundException for a resource type and ID.
     * 
     * @param resourceType the type of resource (e.g., "User", "Course")
     * @param id the ID of the resource that was not found
     */
    public ResourceNotFoundException(String resourceType, UUID id) {
        super(String.format("%s not found with ID: %s", resourceType, id));
    }

    /**
     * Constructs a ResourceNotFoundException for a resource type, field name, and value.
     * 
     * @param resourceType the type of resource (e.g., "User", "Course")
     * @param fieldName the name of the field used in the search
     * @param fieldValue the value that was searched for
     */
    public ResourceNotFoundException(String resourceType, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: %s", resourceType, fieldName, fieldValue));
    }
}

