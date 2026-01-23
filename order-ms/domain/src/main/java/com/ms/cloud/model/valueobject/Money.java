package com.ms.cloud.model.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

public record Money(BigDecimal amount, Currency currency) {

    public static final Money ZERO_USD = new Money(BigDecimal.ZERO, Currency.getInstance("USD"));
    public static final Money ZERO_EUR = new Money(BigDecimal.ZERO, Currency.getInstance("EUR"));
    public static final Money ZERO_COP = new Money(BigDecimal.ZERO, Currency.getInstance("COP"));

    public Money {
        Objects.requireNonNull(amount, "Amount cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");

        // Valida escala (máximo 2 decimales para divisas estándar)
        if (amount.scale() > maxScaleForCurrency(currency)) {
            throw new IllegalArgumentException(
                    String.format("Amount exceeds max decimals for %s: %d",
                            currency.getCurrencyCode(), maxScaleForCurrency(currency))
            );
        }

        // Valida valor negativo (configurable según reglas de negocio)
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Negative amounts are not allowed");
        }

        // Normaliza escala (ej: 100.0000 -> 100.00)
        amount = amount.setScale(maxScaleForCurrency(currency), RoundingMode.HALF_EVEN);
    }

    // === FACTORÍAS PARA USO EN DOMINIO ===
    public static Money zero() {
        return ZERO_COP; // Divisa por defecto del sistema (configurable)
    }

    public static Money of(BigDecimal amount) {
        return new Money(amount, Currency.getInstance("COP")); // Divisa por defecto
    }

    public static Money of(double amount) {
        return of(BigDecimal.valueOf(amount));
    }

    public static Money usd(BigDecimal amount) {
        return new Money(amount, Currency.getInstance("USD"));
    }

    // === OPERACIONES SEGURAS (validan divisas antes de operar) ===
    public Money add(Money other) {
        validateSameCurrency(other);
        return new Money(
                this.amount.add(other.amount).setScale(scale(), RoundingMode.HALF_EVEN),
                this.currency
        );
    }

    public Money subtract(Money other) {
        validateSameCurrency(other);
        return new Money(
                this.amount.subtract(other.amount).setScale(scale(), RoundingMode.HALF_EVEN),
                this.currency
        );
    }

    public Money multiply(double factor) {
        return multiply(BigDecimal.valueOf(factor));
    }

    public Money multiply(BigDecimal factor) {
        return new Money(
                this.amount.multiply(factor).setScale(scale(), RoundingMode.HALF_EVEN),
                this.currency
        );
    }

    // === COMPARACIONES ===
    public boolean isGreaterThan(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean isGreaterOrEqualThan(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) >= 0;
    }

    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }

    // === GETTERS PARA OPERACIONES (sin exponer estado mutable) ===
    public int scale() {
        return maxScaleForCurrency(this.currency);
    }

    public BigDecimal value() {
        return amount; // Devuelve copia inmutable (BigDecimal es inmutable)
    }

    // === MÉTODOS PARA SERIALISACIÓN/PERSISTENCIA ===
    /**
     * Para frameworks (Jackson, JPA) - solo lectura
     */
    protected Money() {
        this(BigDecimal.ZERO, Currency.getInstance("COP"));
    }

    /**
     * Para serialización JSON (ej: Jackson)
     */
    public String currencyCode() {
        return currency.getCurrencyCode();
    }

    // === VALIDACIONES INTERNAS ===
    private void validateSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                    String.format("Cannot operate with different currencies: %s vs %s",
                            this.currency.getCurrencyCode(), other.currency.getCurrencyCode())
            );
        }
    }

    private int maxScaleForCurrency(Currency currency) {
        return switch(currency.getCurrencyCode()) {
            case "JPY" -> 0; // Yen japonés no tiene decimales
            case "CLF" -> 4; // Unidad de Fomento chilena
            default -> 2;   // USD, EUR, COP, etc.
        };
    }

    @Override
    public String toString() {
        return String.format("%s %s", currency.getCurrencyCode(), amount);
    }
}
