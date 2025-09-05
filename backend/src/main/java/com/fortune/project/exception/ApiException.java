package com.fortune.project.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException{
    private String error;

    public ApiException(String message, String error) {
        super(message);
        this.error = error;
    }
    public ApiException(String message) {
        super(message);
    }


}
