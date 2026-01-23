//package com.ms.cloud.model;
//
//import com.ms.cloud.model.valueobject.Discount;
//import com.ms.cloud.model.valueobject.Money;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//public record OrderPricing(
//        Money subtotal,
//        List<Discount> appliedDiscounts,
//        Money totalDiscount,
//        Money taxes,
//        Money finalAmount
//) {
//
//    public static OrderPricing initial() {
//        return new OrderPricing(
//                Money.zero(),
//                Collections.emptyList(),
//                Money.zero(),
//                Money.zero(),
//                Money.zero()
//        );
//    }
//
//    public OrderPricing recalculate(List<OrderItem> items) {
//        Money newSubtotal = items.stream()
//                .map(OrderItem::getTotalPrice)
//                .reduce(Money.zero(), Money::add);
//
//        return new OrderPricing(
//                newSubtotal,
//                this.appliedDiscounts,
//                this.totalDiscount,
//                this.taxes,
//                newSubtotal.subtract(this.totalDiscount).add(this.taxes)
//        );
//    }
//
//    public OrderPricing applyDiscount(Discount discount) {
//        List<Discount> newDiscounts = new ArrayList<>(this.appliedDiscounts);
//        newDiscounts.add(discount);
//
//        Money newTotalDiscount = newDiscounts.stream()
//                .map(Discount::getAmount)
//                .reduce(Money.zero(), Money::add);
//
//        Money newFinalAmount = this.subtotal
//                .subtract(newTotalDiscount)
//                .add(this.taxes);
//
//        return new OrderPricing(
//                this.subtotal,
//                Collections.unmodifiableList(newDiscounts),
//                newTotalDiscount,
//                this.taxes,
//                newFinalAmount
//        );
//    }
//
//
//}
