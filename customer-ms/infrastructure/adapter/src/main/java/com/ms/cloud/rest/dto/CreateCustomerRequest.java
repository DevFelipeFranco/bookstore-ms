package com.ms.cloud.rest.dto;

public record CreateCustomerRequest(
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String street,
        String city,
        String state,
        String zipCode,
        String country) {
}
