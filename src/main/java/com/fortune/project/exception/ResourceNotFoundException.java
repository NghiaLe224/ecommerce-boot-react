package com.fortune.project.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ResourceNotFoundException extends RuntimeException{
    private String resourceName;
    private String field;
    private String fieldName;
    private Long fieldId;

    public ResourceNotFoundException(String resourceName, String field, String fieldName) {
        super(String.format("%s not found with %s: %s",
                resourceName, field, fieldName));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldName = fieldName;
    }

    public ResourceNotFoundException(String resourceName, String field, Long fieldId) {
        super(String.format("%s not found with %s: %d",
                resourceName, field, fieldId));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldId = fieldId;
    }



}
