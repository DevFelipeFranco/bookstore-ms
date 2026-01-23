package com.ms.cloud.ports.in.command;

public record UpdateAddressCommand(
        String customerId,
        String street,
        String city,
        String state,
        String zipCode,
        String country
) {
}
