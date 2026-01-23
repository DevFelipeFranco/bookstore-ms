package com.ms.cloud.model;

import com.ms.cloud.model.valueobject.Address;
import com.ms.cloud.model.valueobject.CreditLimit;
import com.ms.cloud.model.valueobject.CustomerId;
import com.ms.cloud.model.valueobject.Email;
import com.ms.cloud.model.valueobject.PersonalInfo;
import com.ms.cloud.model.valueobject.PurchaseHistory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Builder para reconstruir Customer desde persistencia.
 * Proporciona una API fluida y validación en tiempo de construcción.
 */
public class CustomerBuilder {
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

    CustomerBuilder() {
        // Package-private constructor, use Customer.builder()
    }

    public CustomerBuilder withId(CustomerId id) {
        this.id = id;
        return this;
    }

    public CustomerBuilder withPersonalInfo(PersonalInfo personalInfo) {
        this.personalInfo = personalInfo;
        return this;
    }

    public CustomerBuilder withEmail(Email email) {
        this.email = email;
        return this;
    }

    public CustomerBuilder withAddress(Address address) {
        this.address = address;
        return this;
    }

    public CustomerBuilder withCreditLimit(CreditLimit creditLimit) {
        this.creditLimit = creditLimit;
        return this;
    }

    public CustomerBuilder withType(CustomerType type) {
        this.type = type;
        return this;
    }

    public CustomerBuilder withStatus(CustomerStatus status) {
        this.status = status;
        return this;
    }

    public CustomerBuilder withPurchaseHistory(List<PurchaseHistory> purchaseHistory) {
        this.purchaseHistory = purchaseHistory != null ? new ArrayList<>(purchaseHistory) : new ArrayList<>();
        return this;
    }

    public CustomerBuilder withCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public CustomerBuilder withUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    /**
     * Construye el Customer validando todos los campos requeridos.
     *
     * @return Customer reconstruido
     * @throws IllegalStateException si faltan campos requeridos
     */
    public Customer build() {
        validateRequiredFields();
        return Customer.reconstruct(
                id,
                personalInfo,
                email,
                address,
                creditLimit,
                type,
                status,
                purchaseHistory != null ? purchaseHistory : new ArrayList<>(),
                createdAt,
                updatedAt);
    }

    private void validateRequiredFields() {
        Objects.requireNonNull(id, "Customer ID is required");
        Objects.requireNonNull(personalInfo, "PersonalInfo is required");
        Objects.requireNonNull(email, "Email is required");
        Objects.requireNonNull(address, "Address is required");
        Objects.requireNonNull(creditLimit, "CreditLimit is required");
        Objects.requireNonNull(type, "CustomerType is required");
        Objects.requireNonNull(status, "CustomerStatus is required");
        Objects.requireNonNull(createdAt, "CreatedAt is required");
        Objects.requireNonNull(updatedAt, "UpdatedAt is required");
    }
}
