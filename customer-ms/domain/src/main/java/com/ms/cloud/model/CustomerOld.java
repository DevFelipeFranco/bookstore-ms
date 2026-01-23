package com.ms.cloud.model;

import com.ms.cloud.model.valueobject.CustomerId;

public record CustomerOld(
        CustomerId customerId,
        String customerName
) {
}
