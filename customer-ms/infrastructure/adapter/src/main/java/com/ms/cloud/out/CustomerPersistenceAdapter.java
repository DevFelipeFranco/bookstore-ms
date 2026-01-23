package com.ms.cloud.out;

import com.ms.cloud.model.Customer;
import com.ms.cloud.model.valueobject.CustomerId;
import com.ms.cloud.model.valueobject.Email;
import com.ms.cloud.ports.spi.CustomerRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CustomerPersistenceAdapter implements CustomerRepository {

    @Override
    public Customer save(Customer customer) {
        return null;
    }

    @Override
    public Optional<Customer> findById(CustomerId id) {
        return Optional.empty();
    }

    @Override
    public Optional<Customer> findByEmail(Email email) {
        return Optional.empty();
    }

    @Override
    public boolean existsByEmail(Email email) {
        return false;
    }
}
