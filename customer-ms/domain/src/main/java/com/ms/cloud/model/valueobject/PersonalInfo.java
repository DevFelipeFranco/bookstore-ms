package com.ms.cloud.model.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Value Object que representa la información personal de un usuario.
 * Inmutable y validado al momento de la creación.
 *
 * @param firstName Nombre del usuario (2-50 caracteres, no vacío)
 * @param lastName  Apellido del usuario (2-50 caracteres, no vacío)
 * @param phoneNumber Número de teléfono válido
 */
public record PersonalInfo(
        String firstName,
        String lastName,
        String phoneNumber
) {
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 50;
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9()-\\s]{7,15}$");

    /**
     * Factoría para crear una instancia validada de PersonalInfo.
     *
     * @param firstName Nombre del usuario
     * @param lastName Apellido del usuario
     * @param phoneNumber Número de teléfono
     * @return Nueva instancia de PersonalInfo validada
     * @throws IllegalArgumentException si alguna validación falla
     */
    public static PersonalInfo of(String firstName, String lastName, String phoneNumber) {
        return new PersonalInfo(
                validateAndNormalizeName(firstName, "First name"),
                validateAndNormalizeName(lastName, "Last name"),
                validateAndNormalizePhone(phoneNumber)
        );
    }

    /**
     * Constructor compacto que realiza validaciones adicionales de null.
     * Se ejecuta automáticamente después del constructor canónico.
     *
     * @throws IllegalArgumentException si alguna validación falla
     */
    public PersonalInfo {
        Objects.requireNonNull(firstName, "First name cannot be null");
        Objects.requireNonNull(lastName, "Last name cannot be null");
        Objects.requireNonNull(phoneNumber, "Phone number cannot be null");
    }

    /**
     * Obtiene el nombre completo en formato "Nombre Apellido".
     *
     * @return Nombre completo del usuario
     */
    public String getFullName() {
        return "%s %s".formatted(firstName, lastName);
    }

    /**
     * Valida el formato de un nombre según las reglas de negocio.
     *
     * @param name      Valor a validar
     * @param fieldName Nombre del campo para mensajes de error
     * @return El nombre validado y normalizado
     * @throws IllegalArgumentException si la validación falla
     */
    private static String validateAndNormalizeName(String name, String fieldName) {
        return Stream.ofNullable(name)
                .map(String::strip)
                .filter(n -> !n.isEmpty())
                .filter(n -> n.length() >= MIN_NAME_LENGTH && n.length() <= MAX_NAME_LENGTH)
                .findFirst() // Convertir Stream a Optional
                .orElseThrow(() -> new IllegalArgumentException(
                        "%s must be between %d and %d characters and cannot be empty".formatted(
                                fieldName, MIN_NAME_LENGTH, MAX_NAME_LENGTH
                        )
                ));
    }

    /**
     * Valida y normaliza un número de teléfono.
     *
     * @param phone Valor a validar
     * @return El número de teléfono validado y normalizado
     * @throws IllegalArgumentException si la validación falla
     */
    private static String validateAndNormalizePhone(String phone) {
        return Stream.ofNullable(phone)
                .map(String::strip)
                .filter(p -> !p.isEmpty())
                .filter(PHONE_PATTERN.asMatchPredicate())
                .findFirst() // Convertir Stream a Optional
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid phone number format. Must contain only numbers and symbols +()-, "
                                + "and be between 7-15 characters"
                ));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonalInfo that = (PersonalInfo) o;
        return Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(phoneNumber, that.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, phoneNumber);
    }

    @Override
    public String toString() {
        return "PersonalInfo[firstName=%s, lastName=%s, phoneNumber=%s]".formatted(
                firstName, lastName, phoneNumber
        );
    }
}