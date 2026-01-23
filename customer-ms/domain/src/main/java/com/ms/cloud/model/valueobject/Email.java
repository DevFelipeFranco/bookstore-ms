package com.ms.cloud.model.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public record Email(String value) {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]{1,64}@[A-Za-z0-9.-]{1,255}\\.[A-Za-z]{2,63}$"
    );
    private static final int MAX_LOCAL_PART_LENGTH = 64;
    private static final int MAX_DOMAIN_LENGTH = 255;

    /**
     * Factoría para crear una instancia validada de Email.
     * Normaliza el valor convirtiéndolo a minúsculas y eliminando espacios.
     *
     * @param email Valor del correo electrónico a validar
     * @return Nueva instancia de Email validada y normalizada
     * @throws IllegalArgumentException si el formato es inválido o está vacío
     */
    public static Email of(String email) {
        return new Email(validateAndNormalize(email));
    }

    /**
     * Constructor compacto que realiza validaciones adicionales de null.
     * Se ejecuta automáticamente después del constructor canónico.
     *
     * @throws IllegalArgumentException si el valor es null
     */
    public Email {
        Objects.requireNonNull(value, "Email value cannot be null");
    }

    /**
     * Valida y normaliza un correo electrónico según las reglas de negocio.
     *
     * @param email Valor a validar y normalizar
     * @return Correo electrónico validado y normalizado en minúsculas
     * @throws IllegalArgumentException si la validación falla
     */
    private static String validateAndNormalize(String email) {
        return Stream.ofNullable(email)
                .map(String::strip)           // Manejo nulo seguro y trim mejorado
                .filter(e -> !e.isEmpty())    // Validación de no vacío
                .filter(e -> {                // Validación de longitud por partes
                    int atIndex = e.indexOf('@');
                    if (atIndex == -1) return false;
                    String localPart = e.substring(0, atIndex);
                    String domain = e.substring(atIndex + 1);
                    return localPart.length() <= MAX_LOCAL_PART_LENGTH &&
                            domain.length() <= MAX_DOMAIN_LENGTH;
                })
                .filter(EMAIL_PATTERN.asMatchPredicate()) // Validación regex eficiente
                .map(String::toLowerCase)     // Normalización consistente
                .findFirst()                  // Convertir Stream a Optional
                .orElseThrow(() -> new IllegalArgumentException("Invalid email format"));
    }

    /**
     * Extrae el nombre de usuario (parte local) del correo electrónico.
     *
     * @return Nombre de usuario (ej: "user" de "user@example.com")
     */
    public String getUsername() {
        return value.substring(0, value.indexOf('@'));
    }

    /**
     * Extrae el dominio del correo electrónico.
     *
     * @return Dominio (ej: "example.com" de "user@example.com")
     */
    public String getDomain() {
        return value.substring(value.indexOf('@') + 1);
    }

    /**
     * Verifica si este email pertenece a un dominio específico.
     *
     * @param domain Dominio a verificar (ej: "gmail.com")
     * @return true si pertenece al dominio, false en caso contrario
     */
    public boolean isFromDomain(String domain) {
        return getDomain().equalsIgnoreCase(domain.toLowerCase().strip());
    }

    /**
     * Crea un email de contacto con prefijo para el mismo dominio.
     *
     * @param prefix Prefijo del nuevo email (ej: "support")
     * @return Nuevo Email con formato "prefix@dominio.com"
     */
    public Email withPrefix(String prefix) {
        return Email.of("%s@%s".formatted(prefix.strip().toLowerCase(), getDomain()));
    }

    @Override
    public String toString() {
        return "Email[value='%s']".formatted(value);
    }
}
