package com.ms.cloud.model.valueobject;

import com.ms.cloud.exception.InvalidOrderIdException;

public record OrderId(String value) {

    public OrderId {
        value = validate(value);
    }

    private String validate(String value) {
        if (value == null) {
            throw new InvalidOrderIdException("OrderId cannot be null");
        }

        String trimmedValue = value.trim();

        if (trimmedValue.isEmpty()) {
            throw new InvalidOrderIdException("OrderId cannot be empty");
        }

        return trimmedValue;
    }
}
