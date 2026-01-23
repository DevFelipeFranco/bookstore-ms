package com.ms.cloud.model;

import com.ms.cloud.event.CustomerPromotedToVipEvent;
import com.ms.cloud.event.DomainEvent;
import com.ms.cloud.exception.*;
import com.ms.cloud.model.valueobject.Address;
import com.ms.cloud.model.valueobject.CreditLimit;
import com.ms.cloud.model.valueobject.CustomerId;
import com.ms.cloud.model.valueobject.Email;
import com.ms.cloud.model.valueobject.Money;
import com.ms.cloud.model.valueobject.PersonalInfo;
import com.ms.cloud.model.valueobject.PurchaseHistory;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate Root que representa un Cliente del sistema.
 * Encapsula toda la lógica de negocio relacionada con clientes.
 */
public class Customer {

    // ============================================
    // CONSTANTES DE NEGOCIO
    // ============================================
    private static final int VIP_MIN_PURCHASES = 10;
    private static final Money VIP_MIN_TOTAL_AMOUNT = Money.of(5000);
    private static final BigDecimal VIP_CREDIT_INCREASE_PERCENTAGE = BigDecimal.valueOf(50);

    // Clock abstraction for testability
    private static Clock clock = Clock.systemDefaultZone();

    // ============================================
    // ESTADO
    // ============================================
    private final CustomerId id;
    private PersonalInfo personalInfo;
    private Email email;
    private Address address;
    private CreditLimit creditLimit;
    private CustomerType type;
    private CustomerStatus status;
    private String deactivationReason;
    private final List<PurchaseHistory> purchaseHistory;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Domain Events
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    // ============================================
    // CONSTRUCTOR PRIVADO
    // ============================================
    private Customer(CustomerId id, PersonalInfo personalInfo, Email email, Address address, CreditLimit creditLimit, CustomerType type) {
        this.id = Objects.requireNonNull(id, "CustomerId is required");
        this.personalInfo = Objects.requireNonNull(personalInfo, "PersonalInfo is required");
        this.email = Objects.requireNonNull(email, "Email is required");
        this.address = Objects.requireNonNull(address, "Address is required");
        this.creditLimit = Objects.requireNonNull(creditLimit, "CreditLimit is required");
        this.type = Objects.requireNonNull(type, "CustomerType is required");
        this.status = CustomerStatus.ACTIVE;
        this.purchaseHistory = new ArrayList<>();
        this.createdAt = LocalDateTime.now(clock);
        this.updatedAt = LocalDateTime.now(clock);
    }

    // ============================================
    // FACTORY METHODS
    // ============================================

    /**
     * Crea un nuevo Customer con valores iniciales.
     *
     * @param personalInfo Información personal validada
     * @param email        Email validado
     * @param address      Dirección validada
     * @return Nuevo Customer con ID generado, tipo REGULAR y crédito inicial
     */
    public static Customer create(PersonalInfo personalInfo, Email email, Address address) {
        validatePersonalInfo(personalInfo);
        CreditLimit initialCredit = CreditLimit.initial();
        return new Customer(
                CustomerId.generate(),
                personalInfo,
                email,
                address,
                initialCredit,
                CustomerType.REGULAR);
    }

    /**
     * Reconstruye un Customer desde persistencia.
     * Preferir usar {@link CustomerBuilder} para mayor claridad.
     */
    public static Customer reconstruct(
            CustomerId id,
            PersonalInfo personalInfo,
            Email email,
            Address address,
            CreditLimit creditLimit,
            CustomerType type,
            CustomerStatus status,
            List<PurchaseHistory> purchaseHistory,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        Customer customer = new Customer(id, personalInfo, email, address, creditLimit, type);
        customer.status = Objects.requireNonNull(status, "CustomerStatus is required");
        customer.purchaseHistory.addAll(purchaseHistory != null ? purchaseHistory : new ArrayList<>());
        // Override timestamps from persistence
        try {
            var createdAtField = Customer.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(customer, createdAt);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // Fallback - field already set in constructor
        }
        customer.updatedAt = updatedAt;
        return customer;
    }

    /**
     * Obtiene un Builder para reconstruir Customer desde persistencia.
     *
     * @return Nuevo CustomerBuilder
     */
    public static CustomerBuilder builder() {
        return new CustomerBuilder();
    }

    // ============================================
    // LÓGICA DE NEGOCIO
    // ============================================

    /**
     * Actualiza el límite de crédito del cliente.
     * Regla: No se puede reducir el crédito si hay deuda pendiente.
     *
     * @param newLimit Nuevo límite de crédito
     * @throws InactiveCustomerException   si el cliente está inactivo
     * @throws InvalidCreditLimitException si intenta reducir con deuda pendiente
     */
    public void updateCreditLimit(CreditLimit newLimit) {
        Objects.requireNonNull(newLimit, "New credit limit is required");

        if (!this.status.isActive()) {
            throw new InactiveCustomerException("Cannot update credit for inactive customer");
        }

        if (newLimit.isLessThan(this.creditLimit) && this.hasOutstandingDebt()) {
            throw new InvalidCreditLimitException("Cannot reduce credit limit with outstanding debt");
        }

        this.creditLimit = newLimit;
        markUpdated();
    }

    /**
     * Promueve al cliente a VIP si cumple los requisitos.
     * Requisitos: >= 10 compras y total >= $5000
     * Beneficio: 50% más de límite de crédito
     *
     * @throws InactiveCustomerException si el cliente está inactivo
     * @throws VipPromotionException     si no cumple los requisitos VIP
     */
    public void promoteToVip() {
        if (!this.status.isActive()) {
            throw new InactiveCustomerException("Cannot promote inactive customer");
        }

        if (!this.meetsVipRequirements()) {
            throw new VipPromotionException(
                    "Customer doesn't meet VIP requirements: min %d purchases and $%s total"
                            .formatted(VIP_MIN_PURCHASES, VIP_MIN_TOTAL_AMOUNT.amount()));
        }

        this.type = CustomerType.VIP;
        this.creditLimit = this.creditLimit.increaseByPercentage(VIP_CREDIT_INCREASE_PERCENTAGE);
        markUpdated();

        // Emit domain event
        registerEvent(CustomerPromotedToVipEvent.create(this.id));
    }

    /**
     * Verifica si el cliente puede realizar una compra por el monto dado.
     *
     * @param amount Monto de la compra
     * @return true si puede realizar la compra
     */
    public boolean canMakePurchase(Money amount) {
        if (!this.status.isActive()) {
            return false;
        }
        return this.creditLimit.hasAvailableCredit(amount);
    }

    /**
     * Registra una nueva compra para el cliente.
     *
     * @param amount  Monto de la compra
     * @param orderId Identificador de la orden
     * @throws InsufficientCreditException si no hay crédito suficiente
     */
    public void registerPurchase(Money amount, String orderId) {
        Objects.requireNonNull(amount, "Purchase amount is required");
        Objects.requireNonNull(orderId, "Order ID is required");

        if (!this.canMakePurchase(amount)) {
            throw new InsufficientCreditException(
                    "Insufficient credit: available $%s, requested $%s"
                            .formatted(getAvailableCredit().amount(), amount.amount()));
        }

        PurchaseHistory purchase = PurchaseHistory.create(orderId, amount);
        this.purchaseHistory.add(purchase);
        this.creditLimit = this.creditLimit.consume(amount);
        markUpdated();
    }

    /**
     * Desactiva el cliente con una razón específica.
     *
     * @param reason Razón de la desactivación (requerida)
     * @throws CustomerDeactivationException si tiene deuda pendiente
     */
    public void deactivate(String reason) {
        Objects.requireNonNull(reason, "Deactivation reason is required");

        if (reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Deactivation reason cannot be empty");
        }

        if (this.hasOutstandingDebt()) {
            throw new CustomerDeactivationException(
                    "Cannot deactivate customer with outstanding debt of $%s"
                            .formatted(this.creditLimit.used().amount()));
        }

        this.deactivationReason = reason.trim();
        this.status = CustomerStatus.INACTIVE;
        markUpdated();
    }

    /**
     * Reactiva un cliente previamente desactivado.
     *
     * @throws CustomerReactivationException si el cliente está bloqueado
     */
    public void reactivate() {
        if (this.status == CustomerStatus.BLOCKED) {
            throw new CustomerReactivationException("Cannot reactivate blocked customer");
        }

        this.status = CustomerStatus.ACTIVE;
        this.deactivationReason = null;
        markUpdated();
    }

    /**
     * Actualiza la dirección del cliente.
     *
     * @param newAddress Nueva dirección
     * @throws InactiveCustomerException si el cliente está inactivo
     */
    public void updateAddress(Address newAddress) {
        Objects.requireNonNull(newAddress, "Address is required");

        if (!this.status.isActive()) {
            throw new InactiveCustomerException("Cannot update address for inactive customer");
        }

        this.address = newAddress;
        markUpdated();
    }

    /**
     * Actualiza la información personal del cliente.
     *
     * @param newInfo Nueva información personal
     */
    public void updatePersonalInfo(PersonalInfo newInfo) {
        validatePersonalInfo(newInfo);
        this.personalInfo = newInfo;
        markUpdated();
    }

    // ============================================
    // MÉTODOS DE CONSULTA
    // ============================================

    public boolean isVip() {
        return this.type == CustomerType.VIP;
    }

    public boolean isActive() {
        return this.status.isActive();
    }

    public Money getTotalPurchaseAmount() {
        return this.purchaseHistory.stream()
                .map(PurchaseHistory::amount)
                .reduce(Money.ZERO, Money::add);
    }

    public int getTotalPurchases() {
        return this.purchaseHistory.size();
    }

    public Money getAvailableCredit() {
        return this.creditLimit.getAvailable();
    }

    private boolean hasOutstandingDebt() {
        return this.creditLimit.hasUsedCredit();
    }

    private boolean meetsVipRequirements() {
        return this.getTotalPurchases() >= VIP_MIN_PURCHASES
                && this.getTotalPurchaseAmount().isGreaterThan(VIP_MIN_TOTAL_AMOUNT);
    }

    private static void validatePersonalInfo(PersonalInfo info) {
        Objects.requireNonNull(info, "Personal info cannot be null");
    }

    private void markUpdated() {
        this.updatedAt = LocalDateTime.now(clock);
    }

    // ============================================
    // DOMAIN EVENTS
    // ============================================

    private void registerEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

    /**
     * Obtiene los eventos de dominio pendientes.
     *
     * @return Copia de la lista de eventos
     */
    public List<DomainEvent> getDomainEvents() {
        return new ArrayList<>(domainEvents);
    }

    /**
     * Limpia los eventos de dominio después de publicarlos.
     */
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    // ============================================
    // TESTABILITY
    // ============================================

    /**
     * Establece el Clock para testing.
     * Solo usar en tests.
     *
     * @param testClock Clock para inyectar
     */
    public static void setClock(Clock testClock) {
        clock = Objects.requireNonNull(testClock, "Clock cannot be null");
    }

    /**
     * Restaura el Clock por defecto.
     */
    public static void resetClock() {
        clock = Clock.systemDefaultZone();
    }

    // ============================================
    // GETTERS
    // ============================================

    public CustomerId getId() {
        return id;
    }

    public PersonalInfo getPersonalInfo() {
        return personalInfo;
    }

    public Email getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    public CreditLimit getCreditLimit() {
        return creditLimit;
    }

    public CustomerType getType() {
        return type;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public String getDeactivationReason() {
        return deactivationReason;
    }

    public List<PurchaseHistory> getPurchaseHistory() {
        return new ArrayList<>(purchaseHistory);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // ============================================
    // IDENTITY (Entities compare by ID)
    // ============================================

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Customer[id=%s, name=%s, type=%s, status=%s]".formatted(
                id, personalInfo.getFullName(), type, status);
    }
}
