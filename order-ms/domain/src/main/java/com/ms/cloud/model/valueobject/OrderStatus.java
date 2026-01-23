package com.ms.cloud.model.valueobject;

import com.ms.cloud.model.AuditTrail;

import java.time.Instant;
import java.util.Objects;

public sealed class OrderStatus permits
        DraftStatus,
        ConfirmedStatus,
        PaidStatus,
        ShippedStatus,
        DeliveredStatus,
        CancelledStatus {

    protected final State state;
    protected final AuditTrail auditTrail;

    protected OrderStatus(State state, AuditTrail auditTrail) {
        this.state = state;
        this.auditTrail = auditTrail;
    }

    // === MÉTODOS PÚBLICOS OBLIGATORIOS ===
    public State getState() {
        return state;
    }

    public boolean isDraft() {
        return this instanceof DraftStatus;
    }

    public boolean isConfirmed() {
        return this instanceof ConfirmedStatus || this instanceof PaidStatus;
    }

    public boolean isFinalState() {
        return this instanceof DeliveredStatus || this instanceof CancelledStatus;
    }

    /**
     * Valida si se puede transicionar a un nuevo estado
     * @throws IllegalStateException si la transición es inválida
     */
    public OrderStatus transitionTo(State newState, String reason) {
        if (!state.getAllowedTransitions().contains(newState)) {
            throw new IllegalStateException(
                    String.format("Invalid transition: %s → %s. Allowed: %s",
                            state.name(), newState.name(), state.getAllowedTransitions())
            );
        }

        AuditTrail newAudit = this.auditTrail.append(
                new AuditTrail.Entry(
                        Instant.now(),
                        "SYSTEM",
                        String.format("State changed from %s to %s. Reason: %s",
                                this.state, newState, reason)
                )
        );

        return createStatus(newState, newAudit);
    }

    // === FÁBRICA PARA NUEVOS ESTADOS ===
    private static OrderStatus createStatus(State state, AuditTrail auditTrail) {
        return switch (state) {
            case DRAFT -> new DraftStatus(auditTrail);
            case CONFIRMED -> new ConfirmedStatus(auditTrail);
            case PAID -> new PaidStatus(auditTrail);
            case SHIPPED -> new ShippedStatus(auditTrail, "N/A", "N/A");
            case DELIVERED -> new DeliveredStatus(auditTrail, java.time.Instant.now());
            case CANCELLED -> new CancelledStatus(auditTrail, "NO_REASON");
        };
    }

    public static OrderStatus initial() {
        AuditTrail initialAudit = AuditTrail.initial()
                .append(new AuditTrail.Entry(
                        java.time.Instant.now(),
                        "SYSTEM",
                        "Order created in DRAFT state"
                ));
        return new DraftStatus(initialAudit);
    }

    // === ENUM INTERNO PARA VALORES BASE (solo para serialización) ===
    public enum State {
        DRAFT("Borrador", "#FFA500"),
        CONFIRMED("Confirmado", "#4682B4"),
        PAID("Pagado", "#32CD32"),
        SHIPPED("Enviado", "#1E90FF"),
        DELIVERED("Entregado", "#228B22"),
        CANCELLED("Cancelado", "#FF0000");

        private final String displayName;
        private final String colorCode;

        State(String displayName, String colorCode) {
            this.displayName = displayName;
            this.colorCode = colorCode;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getColorCode() {
            return colorCode;
        }

        public java.util.List<State> getAllowedTransitions() {
            return switch (this) {
                case DRAFT -> java.util.List.of(CONFIRMED);
                case CONFIRMED -> java.util.List.of(PAID, CANCELLED);
                case PAID -> java.util.List.of(SHIPPED, CANCELLED);
                case SHIPPED -> java.util.List.of(DELIVERED, CANCELLED);
                case DELIVERED, CANCELLED -> java.util.Collections.emptyList();
            };
        }
    }
}

final class DraftStatus extends OrderStatus {
    public DraftStatus(AuditTrail auditTrail) {
        super(State.DRAFT, auditTrail);
    }

    @Override
    public OrderStatus transitionTo(State newState, String reason) {
        if (newState == State.CANCELLED) {
            throw new IllegalStateException("Cannot cancel a draft order directly");
        }
        return super.transitionTo(newState, reason);
    }

    // Equals/HashCode específicos para este estado
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DraftStatus that = (DraftStatus) o;
        return super.equals(that);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }
}

final class ConfirmedStatus extends OrderStatus {
    public ConfirmedStatus(AuditTrail auditTrail) {
        super(State.CONFIRMED, auditTrail);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfirmedStatus that = (ConfirmedStatus) o;
        return super.equals(that);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }
}

final class PaidStatus extends OrderStatus {
    public PaidStatus(AuditTrail auditTrail) {
        super(State.PAID, auditTrail);
    }

    @Override
    public OrderStatus transitionTo(State newState, String reason) {
        if (newState == State.CONFIRMED) {
            throw new IllegalStateException("Cannot revert to CONFIRMED after payment");
        }
        return super.transitionTo(newState, reason);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaidStatus that = (PaidStatus) o;
        return super.equals(that);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }
}

final class ShippedStatus extends OrderStatus {
    private final String trackingNumber;
    private final String carrier;

    public ShippedStatus(AuditTrail auditTrail, String trackingNumber, String carrier) {
        super(State.SHIPPED, auditTrail);
        this.trackingNumber = Objects.requireNonNull(trackingNumber, "Tracking number cannot be null");
        this.carrier = Objects.requireNonNull(carrier, "Carrier cannot be null");
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getCarrier() {
        return carrier;
    }

    public boolean hasValidTracking() {
        return trackingNumber != null && !trackingNumber.trim().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ShippedStatus that = (ShippedStatus) o;
        return Objects.equals(trackingNumber, that.trackingNumber) &&
                Objects.equals(carrier, that.carrier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), trackingNumber, carrier);
    }

    @Override
    public String toString() {
        return "ShippedStatus{" +
                "trackingNumber='" + trackingNumber + '\'' +
                ", carrier='" + carrier + '\'' +
                "} " + super.toString();
    }
}

final class DeliveredStatus extends OrderStatus {
    private final java.time.Instant deliveredAt;

    public DeliveredStatus(AuditTrail auditTrail, java.time.Instant deliveredAt) {
        super(State.DELIVERED, auditTrail);
        this.deliveredAt = Objects.requireNonNull(deliveredAt, "Delivered at cannot be null");
    }

    public java.time.Instant getDeliveredAt() {
        return deliveredAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DeliveredStatus that = (DeliveredStatus) o;
        return Objects.equals(deliveredAt, that.deliveredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), deliveredAt);
    }
}

final class CancelledStatus extends OrderStatus {
    private final String cancellationReason;

    public CancelledStatus(AuditTrail auditTrail, String cancellationReason) {
        super(State.CANCELLED, auditTrail);
        this.cancellationReason = Objects.requireNonNull(cancellationReason, "Cancellation reason cannot be null");
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public boolean wasCancelledByCustomer() {
        return cancellationReason != null && cancellationReason.contains("CUSTOMER_REQUEST");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CancelledStatus that = (CancelledStatus) o;
        return Objects.equals(cancellationReason, that.cancellationReason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), cancellationReason);
    }
}
