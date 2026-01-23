package com.ms.cloud.ports.in.command;

public record PromoteToVipCommand(String customerId) {
    public PromoteToVipCommand {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be empty");
        }
    }
}
