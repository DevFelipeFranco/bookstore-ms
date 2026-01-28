package com.ms.cloud.out;

import com.ms.cloud.entity.CustomerEntity;
import com.ms.cloud.mapper.CustomerMapper;
import com.ms.cloud.model.Customer;
import com.ms.cloud.model.valueobject.CustomerId;
import com.ms.cloud.model.valueobject.Email;
import com.ms.cloud.ports.spi.CustomerRepository;
import com.ms.cloud.repository.CustomerJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CustomerPersistenceAdapter implements CustomerRepository {

    private final CustomerJpaRepository repository;
    private final CustomerMapper mapper;

    public CustomerPersistenceAdapter(CustomerJpaRepository repository, CustomerMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Customer save(Customer customer) {
        CustomerEntity entity = mapper.toEntity(customer);
        CustomerEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Customer> findById(CustomerId id) {
        return repository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Customer> findByEmail(Email email) {
        return repository.findByEmail(email.value())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return repository.existsByEmail(email.value());
    }
}
