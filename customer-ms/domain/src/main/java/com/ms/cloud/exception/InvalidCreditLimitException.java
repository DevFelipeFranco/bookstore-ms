package com.ms.cloud.exception;

public class InvalidCreditLimitException extends RuntimeException {
    public InvalidCreditLimitException(String message) {
        super(message);
    }
}
