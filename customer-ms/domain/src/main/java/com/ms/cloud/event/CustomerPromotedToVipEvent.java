package com.ms.cloud.event;

import com.ms.cloud.model.valueobject.CustomerId;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Evento de dominio emitido cuando un Customer es promovido a VIP.
 */
public record CustomerPromotedToVipEvent(
        UUID eventId,
        CustomerId customerId,
        LocalDateTime occurredOn) implements DomainEvent {

    public CustomerPromotedToVipEvent {
        Objects.requireNonNull(eventId, "Event ID cannot be null");
        Objects.requireNonNull(customerId, "Customer ID cannot be null");
        Objects.requireNonNull(occurredOn, "OccurredOn cannot be null");
    }

    /**
     * Factory method para crear el evento con ID y timestamp automáticos.
     *
     * @param customerId ID del customer promovido
     * @return Nuevo evento
     */
    public static CustomerPromotedToVipEvent create(CustomerId customerId) {
        return new CustomerPromotedToVipEvent(
                UUID.randomUUID(),
                customerId,
                LocalDateTime.now());
    }

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
}
