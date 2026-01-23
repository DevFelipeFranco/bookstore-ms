package com.ms.cloud.rest.dto;

import com.ms.cloud.model.Customer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CustomerResponse(
        String id,
        String fullName,
        String email,
        String status,
        String type,
        BigDecimal availableCredit,
        String creditCurrency) {
    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.getId().value(),
                customer.getPersonalInfo().getFullName(),
                customer.getEmail().value(),
                customer.getStatus().name(),
                customer.getType().name(),
                customer.getAvailableCredit().amount(),
                customer.getAvailableCredit().currency());
    }
}
