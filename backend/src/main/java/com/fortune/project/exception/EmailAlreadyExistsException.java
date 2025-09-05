package com.fortune.project.exception;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super(String.format("Email %s have already existed!", email));
    }
}
