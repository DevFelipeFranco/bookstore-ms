package com.ms.cloud.model;

public enum CustomerStatus {
    ACTIVE,
    INACTIVE,
    BLOCKED;

    public boolean isActive() {
        return this == ACTIVE;
    }
}
