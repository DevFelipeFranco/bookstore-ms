package com.ms.cloud.model;

import com.ms.cloud.exception.*;
import com.ms.cloud.model.valueobject.Address;
import com.ms.cloud.model.valueobject.CustomerId;
import com.ms.cloud.model.valueobject.Email;
import com.ms.cloud.model.valueobject.PersonalInfo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Customer {

    private CustomerId id;
    private PersonalInfo personalInfo;
    private Email email;
    private Address address;
    private CreditLimit creditLimit;
    private CustomerType type;
    private CustomerStatus status;
    private List<PurchaseHistory> purchaseHistory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor privado
    private Customer(
            CustomerId id,
            PersonalInfo personalInfo,
            Email email,
            Address address,
            CreditLimit creditLimit,
            CustomerType type) {
        this.id = id;
        this.personalInfo = personalInfo;
        this.email = email;
        this.address = address;
        this.creditLimit = creditLimit;
        this.type = type;
        this.status = CustomerStatus.ACTIVE;
        this.purchaseHistory = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Factory method - Crear nuevo customer
    public static Customer create(PersonalInfo personalInfo, Email email, Address address) {
        validatePersonalInfo(personalInfo);
        CreditLimit initialCredit = CreditLimit.initial();
        return new Customer(
                null,//CustomerId.generate(),
                personalInfo,
                email,
                address,
                initialCredit,
                CustomerType.REGULAR
        );
    }

    // Reconstruir desde persistencia
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
        customer.status = status;
        customer.purchaseHistory = purchaseHistory != null ? purchaseHistory : new ArrayList<>();
        customer.createdAt = createdAt;
        customer.updatedAt = updatedAt;
        return customer;
    }

    // ============================================
    // LÓGICA DE NEGOCIO
    // ============================================

    // Regla 1: Actualizar límite de crédito
    public void updateCreditLimit(CreditLimit newLimit) {
        if (!this.status.isActive()) {
            throw new InactiveCustomerException("Cannot update credit for inactive customer");
        }

        if (newLimit.isLessThan(this.creditLimit) && this.hasOutstandingDebt()) {
            throw new InvalidCreditLimitException("Cannot reduce credit limit with outstanding debt");
        }

        this.creditLimit = newLimit;
        this.updatedAt = LocalDateTime.now();
    }

    // Regla 2: Promover a VIP
    public void promoteToVip() {
        if (!this.status.isActive()) {
            throw new InactiveCustomerException("Cannot promote inactive customer");
        }

        if (!this.meetsVipRequirements()) {
            throw new VipPromotionException("Customer doesn't meet VIP requirements");
        }

        this.type = CustomerType.VIP;
        this.creditLimit = this.creditLimit.increaseByPercentage(BigDecimal.valueOf(50)); // 50% más crédito
        this.updatedAt = LocalDateTime.now();
    }

    // Regla 3: Validar si puede realizar compra
    public boolean canMakePurchase(Money amount) {
        if (!this.status.isActive()) {
            return false;
        }
        return this.creditLimit.hasAvailableCredit(amount);
    }

    // Regla 4: Registrar compra
    public void registerPurchase(Money amount, String orderId) {
        if (!this.canMakePurchase(amount)) {
            throw new InsufficientCreditException("Insufficient credit for purchase");
        }

        PurchaseHistory purchase = PurchaseHistory.create(orderId, amount);
        this.purchaseHistory.add(purchase);
        this.creditLimit = this.creditLimit.consume(amount);
        this.updatedAt = LocalDateTime.now();
    }

    // Regla 5: Desactivar cliente
    public void deactivate(String reason) {
        if (this.hasOutstandingDebt()) {
            throw new CustomerDeactivationException("Cannot deactivate customer with outstanding debt");
        }

        this.status = CustomerStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    // Regla 6: Reactivar cliente
    public void reactivate() {
        if (this.status == CustomerStatus.BLOCKED) {
            throw new CustomerReactivationException("Cannot reactivate blocked customer");
        }

        this.status = CustomerStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    // Regla 7: Actualizar dirección
    public void updateAddress(Address newAddress) {
        if (!this.status.isActive()) {
            throw new InactiveCustomerException("Cannot update address for inactive customer");
        }

        this.address = newAddress;
        this.updatedAt = LocalDateTime.now();
    }

    // Regla 8: Actualizar información personal
    public void updatePersonalInfo(PersonalInfo newInfo) {
        validatePersonalInfo(newInfo);
        this.personalInfo = newInfo;
        this.updatedAt = LocalDateTime.now();
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
        // Requisitos: más de 10 compras y total > $5000
        return this.getTotalPurchases() >= 10
                && this.getTotalPurchaseAmount().isGreaterThan(Money.of(5000));
    }

    private static void validatePersonalInfo(PersonalInfo info) {
        if (info == null) {
            throw new IllegalArgumentException("Personal info cannot be null");
        }
    }

    // Getters
    public CustomerId getId() { return id; }
    public PersonalInfo getPersonalInfo() { return personalInfo; }
    public Email getEmail() { return email; }
    public Address getAddress() { return address; }
    public CreditLimit getCreditLimit() { return creditLimit; }
    public CustomerType getType() { return type; }
    public CustomerStatus getStatus() { return status; }
    public List<PurchaseHistory> getPurchaseHistory() { return new ArrayList<>(purchaseHistory); }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
