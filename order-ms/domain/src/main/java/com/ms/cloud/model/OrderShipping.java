package com.ms.cloud.model;

import com.ms.cloud.model.valueobject.Money;

public record OrderShipping(
        Address deliveryAddress,
//        ShippingMethod method,
        Money cost
//        EstimatedDeliveryDate estimatedDelivery
) {

    public static OrderShipping create(Address address, /*ShippingMethod method,*/ Money cost) {
//        if (!method.canDeliverTo(address.country())) {
//            throw new ShippingNotAvailableException(address.country().code());
//        }
        return new OrderShipping(
                address,
//                method,
                cost
//                method.calculateEstimatedDelivery()
        );
    }
}
