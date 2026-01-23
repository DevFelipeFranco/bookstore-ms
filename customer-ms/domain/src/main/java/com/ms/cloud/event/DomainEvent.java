package com.ms.cloud.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Interface base para todos los eventos de dominio.
 * Proporciona identificación única y timestamp para cada evento.
 */
public interface DomainEvent {

    /**
     * Identificador único del evento.
     *
     * @return UUID del evento
     */
    UUID getEventId();

    /**
     * Momento en que ocurrió el evento.
     *
     * @return Timestamp del evento
     */
    LocalDateTime getOccurredOn();

    /**
     * Nombre del tipo de evento para serialización/routing.
     *
     * @return Nombre del evento
     */
    default String getEventType() {
        return this.getClass().getSimpleName();
    }
}
