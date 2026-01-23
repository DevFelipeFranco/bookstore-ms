package com.ms.cloud.exception;

public class InvalidOrderIdException extends RuntimeException {

    public InvalidOrderIdException(String message) {
        super(message);
    }
}
