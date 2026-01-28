package com.ms.cloud.model.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object que representa el identificador único de un Customer.
 * Inmutable y auto-validado.
 *
 * @param value Valor del identificador (UUID string)
 */
public record CustomerId(String value) {

    /**
     * Constructor compacto con validación.
     */
    public CustomerId {
        Objects.requireNonNull(value, "CustomerId value cannot be null");
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("CustomerId cannot be empty. Provided: " + value);
        }
        value = value.trim();
    }

    /**
     * Genera un nuevo CustomerId con UUID aleatorio.
     *
     * @return Nuevo CustomerId con valor UUID
     */
    public static CustomerId generate() {
        return new CustomerId(UUID.randomUUID().toString());
    }

    /**
     * Crea un CustomerId desde un valor existente.
     *
     * @param value Valor del ID
     * @return CustomerId validado
     */
    public static CustomerId of(String value) {
        return new CustomerId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
