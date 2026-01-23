package com.ms.cloud.model.valueobject;

import java.math.BigDecimal;
import java.util.Objects;

public sealed abstract class Discount permits
        PercentageDiscount,
        FixedAmountDiscount,
        LoyaltyDiscount,
        VolumeDiscount {

    protected final Money amount;
    protected final String description;
    protected final DiscountType type;
    protected final String policyId; // ID de la política de descuento (para auditoría)

    protected Discount(Money amount, String description, DiscountType type, String policyId) {
        validateAmount(amount);
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null");
        this.description = Objects.requireNonNull(description, "Description cannot be null").trim();
        this.type = Objects.requireNonNull(type, "Type cannot be null");
        this.policyId = Objects.requireNonNull(policyId, "Policy ID cannot be null").trim();

        // Validación de negocio adicional
        if (this.description.length() < 5 || this.description.length() > 100) {
            throw new IllegalArgumentException("Description must be between 5 and 100 characters");
        }
    }

    // === MÉTODOS PÚBLICOS OBLIGATORIOS ===
    public Money getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public DiscountType getType() {
        return type;
    }

    public String getPolicyId() {
        return policyId;
    }

    // === FÁBRICAS PARA CASOS DE USO ESPECÍFICOS ===
    public static PercentageDiscount percentage(
            double percentage,
            String description,
            String policyId
    ) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }
        return new PercentageDiscount(percentage, description, policyId);
    }

    public static FixedAmountDiscount fixed(
            Money amount,
            String description,
            String policyId
    ) {
        return new FixedAmountDiscount(amount, description, policyId);
    }

    public static LoyaltyDiscount loyalty(
            Money amount,
            String description,
            String policyId
    ) {
        return new LoyaltyDiscount(amount, description, policyId);
    }

    public static VolumeDiscount volume(
            Money amount,
            String description,
            String policyId
    ) {
        return new VolumeDiscount(amount, description, policyId);
    }

    @Override
    public String toString() {
        return String.format("%s: %s (%s)",
                type.getDisplayName(),
                amount,
                description
        );
    }

    // === VALIDACIONES INTERNAS ===
    private void validateAmount(Money amount) {
        if (amount.isZero()) {
            throw new IllegalArgumentException("Discount amount cannot be zero");
        }
        if (amount.amount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Discount amount cannot be negative");
        }
    }

    // === TIPOS DE DESCUENTO (ENUM para categorización) ===
    public enum DiscountType {
        PERCENTAGE("Percentage Discount"),
        FIXED("Fixed Amount"),
        LOYALTY("Loyalty Program"),
        VOLUME("Volume Purchase"),
        PROMOTION("Special Promotion");

        private final String displayName;

        DiscountType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}

// === IMPLEMENTACIONES CONCRETAS (ejemplos) ===
final class PercentageDiscount extends Discount {
    private final double percentage;

    PercentageDiscount(double percentage, String description, String policyId) {
        super(calculateAmount(percentage), description, DiscountType.PERCENTAGE, policyId);
        this.percentage = percentage;
    }

    private static Money calculateAmount(double percentage) {
        // En la práctica, este cálculo se haría en el contexto de un pedido
        // Aquí es solo un placeholder - el monto real se calcula en OrderPricing
        return Money.zero();
    }

    public double getPercentage() {
        return percentage;
    }
}

final class FixedAmountDiscount extends Discount {
    FixedAmountDiscount(Money amount, String description, String policyId) {
        super(amount, description, DiscountType.FIXED, policyId);
    }
}

final class LoyaltyDiscount extends Discount {
    private final String loyaltyTier;

    LoyaltyDiscount(Money amount, String description, String policyId) {
        super(amount, description, DiscountType.LOYALTY, policyId);
        this.loyaltyTier = extractTier(description);
    }

    private String extractTier(String description) {
        if (description.contains("VIP")) return "VIP";
        if (description.contains("GOLD")) return "GOLD";
        return "STANDARD";
    }

    public String getLoyaltyTier() {
        return loyaltyTier;
    }
}

final class VolumeDiscount extends Discount {
    private final int minItems;

    VolumeDiscount(Money amount, String description, String policyId) {
        super(amount, description, DiscountType.VOLUME, policyId);
        this.minItems = extractMinItems(description);
    }

    private int extractMinItems(String description) {
        // Ej: "10% por compra mayor a $1000 (mínimo 5 items)"
        String[] parts = description.split("\\(");
        if (parts.length > 1 && parts[1].contains("mínimo")) {
            return Integer.parseInt(parts[1].replaceAll("[^0-9]", ""));
        }
        return 1;
    }

    public int getMinItems() {
        return minItems;
    }
}
