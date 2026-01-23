package com.ms.cloud.ports.in.command;

public record UpdateCreditLimitCommand(
        String customerId,
        double newLimit
) {
}
