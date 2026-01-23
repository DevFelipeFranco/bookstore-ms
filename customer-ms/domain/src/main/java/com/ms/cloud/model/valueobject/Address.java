package com.ms.cloud.model.valueobject;

import com.ms.cloud.model.Country;

import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Value Object que representa una dirección postal completa.
 * Inmutable, validado y normalizado al momento de la creación.
 *
 * @param street Calle y número (ej: "123 Main St")
 * @param city Ciudad (ej: "New York")
 * @param state Estado/Provincia (ej: "NY")
 * @param zipCode Código postal (ej: "10001")
 * @param country País (ej: Country.UNITED_STATES)
 */
public record Address(
        String street,
        String city,
        String state,
        String zipCode,
        Country country
) {
    private static final Pattern ZIP_CODE_PATTERN = Pattern.compile("^[0-9]{5}(?:-[0-9]{4})?$");
    private static final int MIN_FIELD_LENGTH = 2;
    private static final int MAX_FIELD_LENGTH = 100;

    /**
     * Factoría para crear una instancia validada de Address.
     * Normaliza los valores eliminando espacios innecesarios.
     *
     * @param street  Calle y número
     * @param city    Ciudad
     * @param state   Estado/Provincia
     * @param zipCode Código postal
     * @param country País (no puede ser null)
     * @return Nueva instancia de Address validada y normalizada
     * @throws IllegalArgumentException si alguna validación falla
     */
    public static Address of(String street, String city, String state, String zipCode, Country country) {
        return new Address(
                validateAndNormalizeField(street, "Street"),
                validateAndNormalizeField(city, "City"),
                validateAndNormalizeField(state, "State"),
                validateAndNormalizeZipCode(zipCode),
                validateCountry(country)
        );
    }

    /**
     * Constructor compacto que realiza validaciones adicionales de null.
     * Se ejecuta automáticamente después del constructor canónico.
     *
     * @throws IllegalArgumentException si algún componente es null
     */
    public Address {
        Objects.requireNonNull(street, "Street cannot be null");
        Objects.requireNonNull(city, "City cannot be null");
        Objects.requireNonNull(state, "State cannot be null");
        Objects.requireNonNull(zipCode, "Zip code cannot be null");
        Objects.requireNonNull(country, "Country cannot be null");
    }

    /**
     * Valida y normaliza un campo de texto según las reglas de negocio.
     *
     * @param value     Valor a validar
     * @param fieldName Nombre del campo para mensajes de error
     * @return Campo validado y normalizado
     * @throws IllegalArgumentException si la validación falla
     */
    private static String validateAndNormalizeField(String value, String fieldName) {
        return Stream.ofNullable(value)
                .map(String::strip)
                .filter(v -> !v.isEmpty())
                .filter(v -> v.length() >= MIN_FIELD_LENGTH && v.length() <= MAX_FIELD_LENGTH)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "%s must be between %d and %d characters and cannot be empty".formatted(
                                fieldName, MIN_FIELD_LENGTH, MAX_FIELD_LENGTH
                        )
                ));
    }

    /**
     * Valida y normaliza un código postal según formato internacional.
     *
     * @param zipCode Valor a validar
     * @return Código postal validado y normalizado
     * @throws IllegalArgumentException si la validación falla
     */
    private static String validateAndNormalizeZipCode(String zipCode) {
        return Stream.ofNullable(zipCode)
                .map(String::strip)
                .filter(z -> !z.isEmpty())
                .filter(ZIP_CODE_PATTERN.asMatchPredicate())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid zip code format. Must be 5 digits (US format: 12345 or 12345-6789)"
                ));
    }

    /**
     * Valida que el país no sea null.
     *
     * @param country País a validar
     * @return País validado
     * @throws IllegalArgumentException si el país es null
     */
    private static Country validateCountry(Country country) {
        return Objects.requireNonNull(country, "Country cannot be null");
    }

    /**
     * Obtiene la dirección completa en formato legible.
     *
     * @return Dirección completa formateada
     */
    public String getFullAddress() {
        return "%s, %s, %s %s, %s".formatted(
                street, city, state, zipCode, country.name()
        );
    }

    /**
     * Obtiene la dirección en formato corto (ciudad, estado, país).
     *
     * @return Dirección corta formateada
     */
    public String getShortAddress() {
        return "%s, %s, %s".formatted(city, state, country.name());
    }

    /**
     * Verifica si esta dirección está en un país específico.
     *
     * @param countryCode Código de país (ej: "US", "CA")
     * @return true si el país coincide, false en caso contrario
     */
    public boolean isFromCountry(String countryCode) {
        return country.getCode().equalsIgnoreCase(countryCode.strip());
    }

    /**
     * Crea una nueva dirección con la misma información pero diferente código postal.
     *
     * @param newZipCode Nuevo código postal
     * @return Nueva instancia de Address con el nuevo código postal
     */
    public Address withZipCode(String newZipCode) {
        return Address.of(street, city, state, newZipCode, country);
    }

    @Override
    public String toString() {
        return "Address[street='%s', city='%s', state='%s', zipCode='%s', country='%s']".formatted(
                street, city, state, zipCode, country.name()
        );
    }
}
