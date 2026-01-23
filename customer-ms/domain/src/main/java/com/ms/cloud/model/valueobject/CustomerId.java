package com.ms.cloud.model.valueobject;

public record CustomerId(String value) {

    public CustomerId {
        value = validate(value);
    }

    private String validate(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("CustomerId cannot be empty");
        }
        return value;
    }
}
