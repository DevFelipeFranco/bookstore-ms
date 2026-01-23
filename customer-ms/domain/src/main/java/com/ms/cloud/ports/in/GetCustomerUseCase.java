package com.ms.cloud.ports.in;

import com.ms.cloud.model.Customer;

public interface GetCustomerUseCase {
    Customer execute(String customerId);
}
