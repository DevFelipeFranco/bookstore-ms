package com.ms.cloud.model;

import com.ms.cloud.model.valueobject.CustomerId;

public record Customer(
        CustomerId customerId,
        String customerName
) {
}
