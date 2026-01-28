package com.ms.cloud.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
public class CustomerEntity {

    @Id
    @Column(nullable = false, unique = true)
    private String id;

    // Personal Info
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    // Email
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // Address
    @Column(name = "address_street", nullable = false)
    private String addressStreet;

    @Column(name = "address_city", nullable = false)
    private String addressCity;

    @Column(name = "address_state", nullable = false)
    private String addressState;

    @Column(name = "address_zip_code", nullable = false)
    private String addressZipCode;

    @Column(name = "address_country", nullable = false)
    private String addressCountry;

    // Credit Limit
    @Column(name = "credit_limit_total_amount", nullable = false)
    private BigDecimal creditLimitTotalAmount;

    @Column(name = "credit_limit_total_currency", nullable = false)
    private String creditLimitTotalCurrency;

    @Column(name = "credit_limit_used_amount", nullable = false)
    private BigDecimal creditLimitUsedAmount;

    @Column(name = "credit_limit_used_currency", nullable = false)
    private String creditLimitUsedCurrency;

    // Type & Status
    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "deactivation_reason")
    private String deactivationReason;

    // Purchase History
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseHistoryEntity> purchaseHistory = new ArrayList<>();

    // Auditing
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public CustomerEntity() {
    }

    public CustomerEntity(String id, String firstName, String lastName, String phoneNumber, String email,
            String addressStreet, String addressCity, String addressState, String addressZipCode, String addressCountry,
            BigDecimal creditLimitTotalAmount, String creditLimitTotalCurrency, BigDecimal creditLimitUsedAmount,
            String creditLimitUsedCurrency, String type, String status, String deactivationReason,
            List<PurchaseHistoryEntity> purchaseHistory, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.addressStreet = addressStreet;
        this.addressCity = addressCity;
        this.addressState = addressState;
        this.addressZipCode = addressZipCode;
        this.addressCountry = addressCountry;
        this.creditLimitTotalAmount = creditLimitTotalAmount;
        this.creditLimitTotalCurrency = creditLimitTotalCurrency;
        this.creditLimitUsedAmount = creditLimitUsedAmount;
        this.creditLimitUsedCurrency = creditLimitUsedCurrency;
        this.type = type;
        this.status = status;
        this.deactivationReason = deactivationReason;
        this.purchaseHistory = purchaseHistory != null ? purchaseHistory : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Helpers
    public void addPurchaseHistory(PurchaseHistoryEntity purchase) {
        purchaseHistory.add(purchase);
        purchase.setCustomer(this);
    }

    public void removePurchaseHistory(PurchaseHistoryEntity purchase) {
        purchaseHistory.remove(purchase);
        purchase.setCustomer(null);
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddressStreet() {
        return addressStreet;
    }

    public void setAddressStreet(String addressStreet) {
        this.addressStreet = addressStreet;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    public String getAddressState() {
        return addressState;
    }

    public void setAddressState(String addressState) {
        this.addressState = addressState;
    }

    public String getAddressZipCode() {
        return addressZipCode;
    }

    public void setAddressZipCode(String addressZipCode) {
        this.addressZipCode = addressZipCode;
    }

    public String getAddressCountry() {
        return addressCountry;
    }

    public void setAddressCountry(String addressCountry) {
        this.addressCountry = addressCountry;
    }

    public BigDecimal getCreditLimitTotalAmount() {
        return creditLimitTotalAmount;
    }

    public void setCreditLimitTotalAmount(BigDecimal creditLimitTotalAmount) {
        this.creditLimitTotalAmount = creditLimitTotalAmount;
    }

    public String getCreditLimitTotalCurrency() {
        return creditLimitTotalCurrency;
    }

    public void setCreditLimitTotalCurrency(String creditLimitTotalCurrency) {
        this.creditLimitTotalCurrency = creditLimitTotalCurrency;
    }

    public BigDecimal getCreditLimitUsedAmount() {
        return creditLimitUsedAmount;
    }

    public void setCreditLimitUsedAmount(BigDecimal creditLimitUsedAmount) {
        this.creditLimitUsedAmount = creditLimitUsedAmount;
    }

    public String getCreditLimitUsedCurrency() {
        return creditLimitUsedCurrency;
    }

    public void setCreditLimitUsedCurrency(String creditLimitUsedCurrency) {
        this.creditLimitUsedCurrency = creditLimitUsedCurrency;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeactivationReason() {
        return deactivationReason;
    }

    public void setDeactivationReason(String deactivationReason) {
        this.deactivationReason = deactivationReason;
    }

    public List<PurchaseHistoryEntity> getPurchaseHistory() {
        return purchaseHistory;
    }

    public void setPurchaseHistory(List<PurchaseHistoryEntity> purchaseHistory) {
        this.purchaseHistory = purchaseHistory;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
