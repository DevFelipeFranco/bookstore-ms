//package com.ms.cloud.model;
//
//import com.ms.cloud.model.valueobject.OrderId;
//import com.ms.cloud.model.valueobject.OrderStatus;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class Order {
//
//    private OrderId id;
//    private Customer customer;
//    private List<OrderItem> items;
//    private OrderPricing pricing;       // ← Objeto especializado
//    private OrderShipping shipping;      // ← Objeto especializado
//    private OrderStatus status;
//
//
//    private Order(Customer customer) {
////        this.id = OrderId.generate();
//        this.customer = customer;
//        this.items = new ArrayList<>();
//        this.pricing = OrderPricing.initial();
//        this.status = OrderStatus.initial();
//    }
//
//    public static Order create(Customer customer) {
//        if (customer == null) {
//            throw new IllegalArgumentException("Customer cannot be null");
//        }
//        return new Order(customer);
//    }
//
//    // ✅ DELEGA en objetos especializados
////    public void addItem(Product product, Quantity quantity) {
////        validateCanAddItems();
////        OrderItem item = OrderItem.create(product, quantity);
////        this.items.add(item);
////        this.pricing = this.pricing.recalculate(this.items); // Delega cálculo
////    }
//
//    public void confirm() {
//        validateCanConfirm();
//        this.status = this.status.transitionTo(
//                OrderStatus.State.CONFIRMED,
//                "Customer confirmed order via web interface"
//        );
//    }
//
//    private void validateCanConfirm() {
//        if (!this.status.isDraft()) {
//            throw new IllegalStateException("Only draft orders can be confirmed");
//        }
//        if (this.items.isEmpty()) {
//            throw new IllegalStateException("Order cannot be confirmed with no items");
//        }
//    }
//}
