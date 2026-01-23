package com.ms.cloud.model.valueobject;

import com.ms.cloud.exception.InvalidCustomerIdException;

public record CustomerId(String value) {

    public CustomerId {
        value = validate(value);
    }

    private String validate(String value) {
        if (value == null) {
            throw new InvalidCustomerIdException("CustomerId cannot be null");
        }

        String trimmedValue = value.trim();

        if (trimmedValue.isEmpty()) {
            throw new InvalidCustomerIdException("CustomerId cannot be empty");
        }

        return trimmedValue;
    }
}
