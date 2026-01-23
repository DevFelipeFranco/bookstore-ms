//package com.ms.cloud.model;
//
//import com.ms.cloud.model.valueobject.Money;
//
//import java.math.BigDecimal;
//import java.util.Objects;
//import java.util.UUID;
//
//public final class OrderItem {
//
//    // === IDENTIDAD ÚNICA (requerida para auditoría y actualizaciones) ===
//    private final String id; // UUID como string para serialización
//
//    // === REFERENCIA AL PRODUCTO (solo identificadores, ¡nunca objetos completos!) ===
//    private final String productId;
//    private final String productSku; // Stock Keeping Unit (para variantes)
//    private final String productName;
//
//    // === CANTIDAD Y PRECIO (con validaciones estrictas) ===
//    private final Quantity quantity;
//    private final Money unitPrice;
//    private final Money totalPrice; // Calculado automáticamente: quantity * unitPrice
//
//    // === METADATOS PARA AUDITORÍA ===
//    private final String addedAt; // Timestamp ISO 8601 de cuando se añadió al pedido
//    private final String taxCode; // Código de impuesto (ej: "VAT-21", "EXEMPT")
//    private final boolean isGift; // Si es un producto de regalo (afecta cálculos de impuestos)
//
//    /**
//     * Constructor privado - solo accesible mediante factorías
//     */
//    private OrderItem(
//            String id,
//            String productId,
//            String productSku,
//            String productName,
//            Quantity quantity,
//            Money unitPrice,
//            String addedAt,
//            String taxCode,
//            boolean isGift
//    ) {
//        validateConstruction(productId, productSku, productName, quantity, unitPrice);
//
//        this.id = id;
//        this.productId = productId;
//        this.productSku = productSku;
//        this.productName = productName;
//        this.quantity = quantity;
//        this.unitPrice = unitPrice;
//        this.addedAt = addedAt;
//        this.taxCode = taxCode;
//        this.isGift = isGift;
//
//        // Calcula el precio total automáticamente (invariante de negocio)
//        this.totalPrice = calculateTotalPrice();
//    }
//
//    /**
//     * Factoría principal para crear un ítem de pedido
//     *
//     * @param product Producto del catálogo (solo se usan sus valores primitivos)
//     * @param quantity Cantidad solicitada (validada)
//     * @return Nuevo OrderItem listo para usar
//     */
////    public static OrderItem create(Product product, Quantity quantity) {
////        return new OrderItem(
////                generateId(),
////                product.getId().getValue(),
////                product.getSku(),
////                product.getName(),
////                quantity,
////                product.getPrice(), // Precio actual en el momento de añadir al pedido
////                getCurrentTimestamp(),
////                product.getTaxCode(),
////                product.isGiftEligible()
////        );
////    }
//
//    /**
//     * Método requerido por OrderPricing para calcular el subtotal
//     */
//    public Money getTotalPrice() {
//        return this.totalPrice;
//    }
//
//    /**
//     * Crea una copia con nueva cantidad (para actualizaciones)
//     */
//    public OrderItem withQuantity(Quantity newQuantity) {
//        return new OrderItem(
//                this.id,
//                this.productId,
//                this.productSku,
//                this.productName,
//                newQuantity,
//                this.unitPrice,
//                this.addedAt,
//                this.taxCode,
//                this.isGift
//        );
//    }
//
//    // === MÉTODOS INTERNOS ===
//    private Money calculateTotalPrice() {
//        if (this.isGift) {
//            return Money.zero();
//        }
//        return this.unitPrice.multiply(this.quantity.getValue());
//    }
//
//    private void validateConstruction(
//            String productId,
//            String productSku,
//            String productName,
//            Quantity quantity,
//            Money unitPrice
//    ) {
//        Objects.requireNonNull(productId, "Product ID cannot be null");
//        Objects.requireNonNull(productSku, "Product SKU cannot be null");
//        if (productName == null || productName.trim().length() < 2) {
//            throw new IllegalArgumentException("Invalid product name");
//        }
//
//        // Valida cantidad (mínimo 1, máximo 100 por ítem)
//        if (quantity.getValue() < 1) {
//            throw new IllegalArgumentException("Quantity must be at least 1");
//        }
//        if (quantity.getValue() > 100) {
//            throw new IllegalArgumentException("Maximum 100 items per product");
//        }
//
//        // Valida precio (no puede ser cero ni negativo)
//        if (unitPrice.isZero()) {
//            throw new IllegalArgumentException("Product price cannot be zero");
//        }
//        if (unitPrice.amount().compareTo(BigDecimal.ZERO) < 0) {
//            throw new IllegalArgumentException("Negative prices are not allowed");
//        }
//    }
//
//    private static String generateId() {
//        return "item_" + UUID.randomUUID().toString().substring(0, 8);
//    }
//
//    private static String getCurrentTimestamp() {
//        return java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC)
//                .format(java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME);
//    }
//
//    @Override
//    public String toString() {
//        return String.format(
//                "%d x %s (SKU: %s) @ %s = %s",
//                quantity.getValue(),
//                productName,
//                productSku,
//                unitPrice,
//                totalPrice
//        );
//    }
//}
