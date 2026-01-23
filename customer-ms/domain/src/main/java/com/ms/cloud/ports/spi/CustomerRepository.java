package com.ms.cloud.ports.spi;

import com.ms.cloud.model.Customer;
import com.ms.cloud.model.valueobject.CustomerId;
import com.ms.cloud.model.valueobject.Email;

import java.util.Optional;

public interface CustomerRepository {
    Customer save(Customer customer);
    Optional<Customer> findById(CustomerId id);
    Optional<Customer> findByEmail(Email email);
    boolean existsByEmail(Email email);
}
