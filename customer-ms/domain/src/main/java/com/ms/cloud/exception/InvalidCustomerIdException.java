package com.ms.cloud.exception;

public class InvalidCustomerIdException extends RuntimeException {
    public InvalidCustomerIdException(String message) {
        super(message);
    }
}
