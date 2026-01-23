package com.ms.cloud.exception;

public class InactiveCustomerException extends RuntimeException {
    public InactiveCustomerException(String message) {
        super(message);
    }
}
