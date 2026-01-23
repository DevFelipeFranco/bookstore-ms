package com.ms.cloud.exception;

public class CustomerDeactivationException extends RuntimeException {
    public CustomerDeactivationException(String message) {
        super(message);
    }
}
