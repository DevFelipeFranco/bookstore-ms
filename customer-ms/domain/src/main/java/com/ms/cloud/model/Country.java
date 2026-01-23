package com.ms.cloud.model;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Value Object que representa un país con código y nombre.
 * Inmutable y tipo seguro.
 */
public enum Country {
    UNITED_STATES("US", "United States"),
    CANADA("CA", "Canada"),
    MEXICO("MX", "Mexico"),
    UNITED_KINGDOM("GB", "United Kingdom"),
    GERMANY("DE", "Germany"),
    FRANCE("FR", "France"),
    SPAIN("ES", "Spain"),
    ITALY("IT", "Italy"),
    JAPAN("JP", "Japan"),
    AUSTRALIA("AU", "Australia");

    private final String code;
    private final String name;
    private static final Map<String, Country> BY_CODE = Arrays.stream(values())
            .collect(Collectors.toMap(c -> c.code.toLowerCase(), Function.identity()));

    Country(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Obtiene un país por su código ISO.
     *
     * @param code Código ISO del país (ej: "US", "CA")
     * @return País correspondiente
     * @throws IllegalArgumentException si el código no es válido
     */
    public static Country fromCode(String code) {
        if (code == null || code.strip().isEmpty()) {
            throw new IllegalArgumentException("Country code cannot be null or empty");
        }

        String normalizedCode = code.toLowerCase().strip();
        Country country = BY_CODE.get(normalizedCode);

        if (country == null) {
            throw new IllegalArgumentException("Invalid country code: %s".formatted(code));
        }

        return country;
    }

    /**
     * Versión segura para nulos del método fromCode.
     *
     * @param code Código ISO del país o null
     * @return País correspondiente o null si el código es null
     */
    public static Country fromCodeOrNull(String code) {
        try {
            return code == null ? null : fromCode(code);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Verifica si un código de país es válido.
     *
     * @param code Código ISO a verificar
     * @return true si el código es válido, false en caso contrario
     */
    public static boolean isValidCode(String code) {
        try {
            fromCode(code);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
