package com.ms.cloud.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Value Object que representa una cantidad monetaria con moneda.
 * Soporta operaciones matemáticas seguras y precisas.
 *
 * @param amount Monto numérico con 2 decimales de precisión
 * @param currency Código de moneda ISO (ej: "USD", "EUR")
 */
public record Money(BigDecimal amount, String currency) {
    public static final Money ZERO = new Money(BigDecimal.ZERO, "USD");
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    /**
     * Crea un nuevo Money con monto y moneda por defecto (USD).
     *
     * @param amount Monto numérico
     * @return Nuevo Money con moneda USD
     */
    public static Money of(BigDecimal amount) {
        return new Money(amount.setScale(SCALE, ROUNDING_MODE), "USD");
    }

    /**
     * Crea un nuevo Money con monto y moneda específicos.
     *
     * @param amount Monto numérico
     * @param currency Código de moneda ISO
     * @return Nuevo Money validado
     */
    public static Money of(BigDecimal amount, String currency) {
        validateCurrency(currency);
        return new Money(amount.setScale(SCALE, ROUNDING_MODE), currency.toUpperCase());
    }

    /**
     * Crea un nuevo Money desde un double (evitar en producción por precisión).
     *
     * @param amount Monto como double
     * @return Nuevo Money con USD
     */
    public static Money of(double amount) {
        return of(BigDecimal.valueOf(amount));
    }

    /**
     * Constructor compacto con validaciones.
     *
     * @throws IllegalArgumentException si las validaciones fallan
     */
    public Money {
        Objects.requireNonNull(amount, "Amount cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");

        validateAmount(amount);
        validateCurrency(currency);
    }

    /**
     * Valida que el monto sea válido (no NaN, no infinito).
     *
     * @param amount Monto a validar
     * @throws IllegalArgumentException si es inválido
     */
    private static void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        if (amount.scale() > SCALE * 2) { // Permitir algo de flexibilidad para cálculos
            throw new IllegalArgumentException(
                    "Amount has too many decimal places (max %d)".formatted(SCALE * 2)
            );
        }
    }

    /**
     * Valida que el código de moneda sea ISO válido.
     *
     * @param currency Código de moneda
     * @throws IllegalArgumentException si es inválido
     */
    private static void validateCurrency(String currency) {
        try {
            Currency.getInstance(currency.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid currency code: %s".formatted(currency));
        }
    }

    /**
     * Suma dos montos monetarios (deben tener la misma moneda).
     *
     * @param other Monto a sumar
     * @return Nuevo Money con la suma
     * @throws IllegalArgumentException si las monedas difieren o nulo
     */
    public Money add(Money other) {
        Objects.requireNonNull(other, "Cannot add null money");
        validateSameCurrency(other);
        BigDecimal newAmount = this.amount.add(other.amount).setScale(SCALE, ROUNDING_MODE);
        return new Money(newAmount, this.currency);
    }

    /**
     * Resta dos montos monetarios (deben tener la misma moneda).
     *
     * @param other Monto a restar
     * @return Nuevo Money con la resta
     * @throws IllegalArgumentException si las monedas difieren o nulo
     */
    public Money subtract(Money other) {
        Objects.requireNonNull(other, "Cannot subtract null money");
        validateSameCurrency(other);
        BigDecimal newAmount = this.amount.subtract(other.amount).setScale(SCALE, ROUNDING_MODE);
        return new Money(newAmount, this.currency);
    }

    /**
     * Verifica si este monto es mayor que otro.
     *
     * @param other Monto a comparar
     * @return true si este monto es mayor
     */
    public boolean isGreaterThan(Money other) {
        Objects.requireNonNull(other, "Cannot compare with null money");
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    /**
     * Verifica si este monto es menor que otro.
     *
     * @param other Monto a comparar
     * @return true si este monto es menor
     */
    public boolean isLessThan(Money other) {
        Objects.requireNonNull(other, "Cannot compare with null money");
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) < 0;
    }

    /**
     * Verifica si este monto es cero.
     *
     * @return true si el monto es exactamente cero
     */
    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * Verifica si este monto es negativo.
     *
     * @return true si el monto es menor que cero
     */
    public boolean isNegative() {
        return this.amount.compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * Valida que dos montos tengan la misma moneda.
     *
     * @param other Otro Money para comparar
     * @throws IllegalArgumentException si las monedas difieren
     */
    private void validateSameCurrency(Money other) {
        if (!this.currency.equals(other.currency())) {
            throw new IllegalArgumentException(
                    "Currency mismatch: %s vs %s".formatted(this.currency, other.currency())
            );
        }
    }

    @Override
    public String toString() {
        return "%s %s".formatted(amount, currency);
    }
}
