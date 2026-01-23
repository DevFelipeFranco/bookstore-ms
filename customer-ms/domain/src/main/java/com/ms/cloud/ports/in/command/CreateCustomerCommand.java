package com.ms.cloud.ports.in.command;

public record CreateCustomerCommand(
        String firstName,
        String lastName,
        String phoneNumber,
        String email,
        String street,
        String city,
        String state,
        String zipCode,
        String country
) {
}
