package com.ms.cloud.model;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Value Object que representa un límite de crédito con total y monto utilizado.
 * Inmutable y con validaciones robustas de negocio.
 *
 * @param total Monto total del límite de crédito
 * @param used Monto ya utilizado del crédito
 */
public record CreditLimit(Money total, Money used) {
    private static final BigDecimal MAX_PERCENTAGE_INCREASE = new BigDecimal("100.00");
    private static final BigDecimal MIN_PERCENTAGE = BigDecimal.ZERO;
    private static final Money DEFAULT_INITIAL_LIMIT = Money.of(1000);

    /**
     * Crea un nuevo límite de crédito con validación completa.
     *
     * @param total Monto total del crédito
     * @param used  Monto utilizado
     * @return Nuevo CreditLimit validado
     * @throws IllegalArgumentException si las validaciones de negocio fallan
     */
    public static CreditLimit of(Money total, Money used) {
        validateCreditParameters(total, used);
        return new CreditLimit(total, used);
    }

    /**
     * Crea un límite de crédito inicial con valor por defecto.
     *
     * @return CreditLimit inicial con $1000 total y $0 utilizado
     */
    public static CreditLimit initial() {
        return new CreditLimit(DEFAULT_INITIAL_LIMIT, Money.ZERO);
    }

    /**
     * Constructor compacto que realiza validaciones de negocio.
     * Se ejecuta automáticamente después del constructor canónico.
     *
     * @throws IllegalArgumentException si las validaciones fallan
     */
    public CreditLimit {
        Objects.requireNonNull(total, "Total credit amount cannot be null");
        Objects.requireNonNull(used, "Used credit amount cannot be null");

        validateSameCurrency(total, used);
        validateCreditUsage(total, used);
    }

    /**
     * Valida que los parámetros de crédito sean válidos antes de la construcción.
     *
     * @param total Monto total
     * @param used  Monto utilizado
     * @throws IllegalArgumentException si las validaciones fallan
     */
    private static void validateCreditParameters(Money total, Money used) {
        Stream.ofNullable(total)
                .filter(t -> t.isGreaterThan(Money.ZERO))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Total credit must be greater than zero"));

        Stream.ofNullable(used)
                .filter(u -> !u.isNegative())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Used credit cannot be negative"));
    }

    /**
     * Valida que ambos montos usen la misma moneda.
     *
     * @param total Monto total
     * @param used  Monto utilizado
     * @throws IllegalArgumentException si las monedas difieren
     */
    private static void validateSameCurrency(Money total, Money used) {
        if (!total.currency().equals(used.currency())) {
            throw new IllegalArgumentException(
                    "Currency mismatch: total is in %s but used is in %s".formatted(
                            total.currency(), used.currency()
                    )
            );
        }
    }

    /**
     * Valida que el crédito utilizado no exceda el total.
     *
     * @param total Monto total
     * @param used  Monto utilizado
     * @throws IllegalArgumentException si used > total
     */
    private static void validateCreditUsage(Money total, Money used) {
        if (used.isGreaterThan(total)) {
            throw new IllegalArgumentException(
                    "Used credit ($%s) cannot exceed total credit ($%s)".formatted(
                            used.amount(), total.amount()
                    )
            );
        }
    }

    /**
     * Consume una cantidad adicional del crédito disponible.
     *
     * @param amount Monto a consumir
     * @return Nuevo CreditLimit con el consumo aplicado
     * @throws IllegalArgumentException si excede el crédito disponible o parámetros inválidos
     */
    public CreditLimit consume(Money amount) {
        validateConsumption(amount);
        Money newUsed = this.used.add(amount);
        return CreditLimit.of(this.total, newUsed);
    }

    /**
     * Libera una cantidad del crédito utilizado.
     *
     * @param amount Monto a liberar
     * @return Nuevo CreditLimit con la liberación aplicada
     * @throws IllegalArgumentException si la liberación excede el crédito utilizado
     */
    public CreditLimit release(Money amount) {
        validateRelease(amount);
        Money newUsed = this.used.subtract(amount);
        return CreditLimit.of(this.total, newUsed);
    }

    /**
     * Aumenta el límite total en un porcentaje especificado.
     *
     * @param percentage Porcentaje de aumento (ej: 10.0 para 10%)
     * @return Nuevo CreditLimit con el aumento aplicado
     * @throws IllegalArgumentException si el porcentaje es inválido
     */
    public CreditLimit increaseByPercentage(BigDecimal percentage) {
        validatePercentage(percentage);

        BigDecimal increaseFactor = percentage.divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal newTotalAmount = this.total.amount()
                .multiply(BigDecimal.ONE.add(increaseFactor))
                .setScale(2, BigDecimal.ROUND_HALF_UP);

        Money newTotal = Money.of(newTotalAmount, this.total.currency());
        return CreditLimit.of(newTotal, this.used);
    }

    /**
     * Obtiene el crédito disponible restante.
     *
     * @return Monto disponible para consumo
     */
    public Money getAvailable() {
        return this.total.subtract(this.used);
    }

    /**
     * Verifica si hay crédito disponible para un monto específico.
     *
     * @param amount Monto a verificar
     * @return true si hay suficiente crédito disponible, false en caso contrario
     * @throws IllegalArgumentException si el monto es inválido
     */
    public boolean hasAvailableCredit(Money amount) {
        Objects.requireNonNull(amount, "Amount to check cannot be null");
        if (amount.isNegative()) {
            throw new IllegalArgumentException("Amount to check cannot be negative");
        }
        return !this.used.add(amount).isGreaterThan(this.total);
    }

    /**
     * Verifica si se ha utilizado algún crédito.
     *
     * @return true si hay crédito utilizado (> 0), false en caso contrario
     */
    public boolean hasUsedCredit() {
        return !this.used.isZero();
    }

    /**
     * Compara si este límite de crédito es menor que otro.
     * Solo compara el monto total, no el utilizado.
     *
     * @param other Otro CreditLimit para comparar
     * @return true si este total es menor que el total del otro
     */
    public boolean isLessThan(CreditLimit other) {
        Objects.requireNonNull(other, "Cannot compare with null CreditLimit");
        return this.total.isLessThan(other.total);
    }

    /**
     * Valida que un consumo sea válido.
     *
     * @param amount Monto a consumir
     * @throws IllegalArgumentException si la validación falla
     */
    private void validateConsumption(Money amount) {
        Objects.requireNonNull(amount, "Consumption amount cannot be null");
        if (amount.isNegative() || amount.isZero()) {
            throw new IllegalArgumentException("Consumption amount must be positive");
        }
        if (!hasAvailableCredit(amount)) {
            throw new IllegalArgumentException(
                    "Insufficient credit: available $%s, requested $%s".formatted(
                            getAvailable().amount(), amount.amount()
                    )
            );
        }
        validateSameCurrency(this.total, amount);
    }

    /**
     * Valida que una liberación sea válida.
     *
     * @param amount Monto a liberar
     * @throws IllegalArgumentException si la validación falla
     */
    private void validateRelease(Money amount) {
        Objects.requireNonNull(amount, "Release amount cannot be null");
        if (amount.isNegative() || amount.isZero()) {
            throw new IllegalArgumentException("Release amount must be positive");
        }
        if (amount.isGreaterThan(this.used)) {
            throw new IllegalArgumentException(
                    "Cannot release $%s when only $%s is used".formatted(
                            amount.amount(), this.used.amount()
                    )
            );
        }
        validateSameCurrency(this.total, amount);
    }

    /**
     * Valida que un porcentaje sea válido para aumento.
     *
     * @param percentage Porcentaje a validar
     * @throws IllegalArgumentException si el porcentaje es inválido
     */
    private void validatePercentage(BigDecimal percentage) {
        Objects.requireNonNull(percentage, "Percentage cannot be null");

        if (percentage.compareTo(MIN_PERCENTAGE) < 0) {
            throw new IllegalArgumentException("Percentage cannot be negative");
        }

        if (percentage.compareTo(MAX_PERCENTAGE_INCREASE) > 0) {
            throw new IllegalArgumentException(
                    "Percentage cannot exceed %s%%".formatted(MAX_PERCENTAGE_INCREASE)
            );
        }
    }

    @Override
    public String toString() {
        return "CreditLimit[total=$%s, used=$%s, available=$%s]".formatted(
                total.amount(), used.amount(), getAvailable().amount()
        );
    }
}
