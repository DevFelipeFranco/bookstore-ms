package com.ms.cloud.model.valueobject;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Value Object que representa un historial de compra con información de orden.
 * Inmutable y con validaciones robustas de negocio.
 *
 * @param orderId Identificador único de la orden
 * @param amount Monto de la compra
 * @param purchaseDate Fecha y hora de la compra
 */
public record PurchaseHistory(String orderId, Money amount, LocalDateTime purchaseDate) {
    private static final int MIN_ORDER_ID_LENGTH = 3;
    private static final int MAX_ORDER_ID_LENGTH = 50;

    /**
     * Crea un nuevo historial de compra con fecha actual.
     *
     * @param orderId Identificador de la orden
     * @param amount  Monto de la compra
     * @return Nuevo PurchaseHistory con fecha actual
     * @throws IllegalArgumentException si las validaciones fallan
     */
    public static PurchaseHistory create(String orderId, Money amount) {
        return new PurchaseHistory(
                validateOrderId(orderId),
                validateAmount(amount),
                getCurrentDateTime()
        );
    }

    /**
     * Reconstruye un historial de compra existente con fecha específica.
     * Usado para cargar datos desde persistencia o sistemas externos.
     *
     * @param orderId      Identificador de la orden
     * @param amount       Monto de la compra
     * @param purchaseDate Fecha y hora de la compra
     * @return PurchaseHistory reconstruido
     * @throws IllegalArgumentException si las validaciones fallan
     */
    public static PurchaseHistory reconstruct(String orderId, Money amount, LocalDateTime purchaseDate) {
        return new PurchaseHistory(
                validateOrderId(orderId),
                validateAmount(amount),
                validatePurchaseDate(purchaseDate)
        );
    }

    /**
     * Constructor compacto que realiza validaciones adicionales.
     * Se ejecuta automáticamente después del constructor canónico.
     *
     * @throws IllegalArgumentException si las validaciones fallan
     */
    public PurchaseHistory {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(amount, "Amount cannot be null");
        Objects.requireNonNull(purchaseDate, "Purchase date cannot be null");

        validateOrderIdLength(orderId);
    }

    /**
     * Valida y normaliza el ID de orden.
     *
     * @param orderId ID a validar
     * @return ID validado y normalizado
     * @throws IllegalArgumentException si la validación falla
     */
    private static String validateOrderId(String orderId) {
        return Stream.ofNullable(orderId)
                .map(String::strip)
                .filter(id -> !id.isEmpty())
                .filter(id -> id.length() >= MIN_ORDER_ID_LENGTH && id.length() <= MAX_ORDER_ID_LENGTH)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order ID must be between %d and %d characters and cannot be empty".formatted(
                                MIN_ORDER_ID_LENGTH, MAX_ORDER_ID_LENGTH
                        )
                ));
    }

    /**
     * Valida el monto de la compra.
     *
     * @param amount Monto a validar
     * @return Monto validado
     * @throws IllegalArgumentException si la validación falla
     */
    private static Money validateAmount(Money amount) {
        return Stream.ofNullable(amount)
                .filter(m -> !m.isNegative() && !m.isZero())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Purchase amount must be positive and greater than zero"
                ));
    }

    /**
     * Valida la fecha de compra.
     *
     * @param purchaseDate Fecha a validar
     * @return Fecha validada
     * @throws IllegalArgumentException si la validación falla
     */
    private static LocalDateTime validatePurchaseDate(LocalDateTime purchaseDate) {
        if (purchaseDate == null) {
            throw new IllegalArgumentException("Purchase date cannot be null");
        }

        LocalDateTime now = getCurrentDateTime();
        if (purchaseDate.isAfter(now.plusDays(1))) {
            throw new IllegalArgumentException(
                    "Purchase date cannot be in the future (current time: %s)".formatted(now)
            );
        }

        if (purchaseDate.isBefore(now.minusYears(10))) {
            throw new IllegalArgumentException(
                    "Purchase date cannot be older than 10 years (current time: %s)".formatted(now)
            );
        }

        return purchaseDate;
    }

    /**
     * Obtiene la fecha y hora actual para nuevas compras.
     * Permite inyección de dependencias para testing.
     *
     * @return Fecha y hora actual en la zona horaria del sistema
     */
    private static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now(ZoneId.systemDefault());
    }

    /**
     * Valida que el ID de orden tenga la longitud correcta.
     *
     * @param orderId ID a validar
     * @throws IllegalArgumentException si la longitud es inválida
     */
    private static void validateOrderIdLength(String orderId) {
        if (orderId.length() < MIN_ORDER_ID_LENGTH || orderId.length() > MAX_ORDER_ID_LENGTH) {
            throw new IllegalArgumentException(
                    "Order ID length must be between %d and %d characters".formatted(
                            MIN_ORDER_ID_LENGTH, MAX_ORDER_ID_LENGTH
                    )
            );
        }
    }

    /**
     * Verifica si esta compra es reciente (menos de 30 días).
     *
     * @return true si la compra es reciente, false en caso contrario
     */
    public boolean isRecent() {
        return this.purchaseDate.isAfter(LocalDateTime.now().minusDays(30));
    }

    /**
     * Verifica si esta compra pertenece a un período específico.
     *
     * @param startDate Fecha de inicio del período
     * @param endDate   Fecha de fin del período
     * @return true si la compra está dentro del período, false en caso contrario
     */
    public boolean isInPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        Objects.requireNonNull(startDate, "Start date cannot be null");
        Objects.requireNonNull(endDate, "End date cannot be null");

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        return !this.purchaseDate.isBefore(startDate) && !this.purchaseDate.isAfter(endDate);
    }

    /**
     * Obtiene el año de la compra.
     *
     * @return Año de la compra
     */
    public int getYear() {
        return this.purchaseDate.getYear();
    }

    /**
     * Obtiene el mes de la compra.
     *
     * @return Mes de la compra (1-12)
     */
    public int getMonth() {
        return this.purchaseDate.getMonthValue();
    }

    /**
     * Formatea la fecha de compra para mostrar al usuario.
     *
     * @return Fecha formateada como "yyyy-MM-dd HH:mm"
     */
    public String getFormattedDate() {
        return this.purchaseDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    /**
     * Crea una nueva compra con el mismo ID y fecha pero diferente monto.
     * Útil para correcciones o ajustes.
     *
     * @param newAmount Nuevo monto
     * @return Nueva instancia de PurchaseHistory
     */
    public PurchaseHistory withAmount(Money newAmount) {
        return PurchaseHistory.reconstruct(this.orderId, newAmount, this.purchaseDate);
    }

    @Override
    public String toString() {
        return "PurchaseHistory[orderId='%s', amount=$%s, purchaseDate='%s']".formatted(
                orderId, amount.amount(), getFormattedDate()
        );
    }
}
