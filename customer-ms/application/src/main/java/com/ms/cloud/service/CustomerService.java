package com.ms.cloud.service;

import com.ms.cloud.model.Country;
import com.ms.cloud.model.Customer;
import com.ms.cloud.model.valueobject.Address;
import com.ms.cloud.model.valueobject.CustomerId;
import com.ms.cloud.model.valueobject.Email;
import com.ms.cloud.model.valueobject.PersonalInfo;
import com.ms.cloud.ports.in.*;
import com.ms.cloud.ports.in.command.CreateCustomerCommand;
import com.ms.cloud.ports.in.command.UpdateAddressCommand;
import com.ms.cloud.ports.in.command.UpdateCreditLimitCommand;
import com.ms.cloud.ports.spi.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerService implements CreateCustomerUseCase, GetCustomerUseCase, UpdateCreditLimitUseCase, /*PromoteToVipUseCase*/ UpdateAddressUseCase {

    private final CustomerRepository customerRepository;

    @Override
    public CustomerId execute(CreateCustomerCommand command) {
        Email email = Email.of(command.email());
        if (customerRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        // 2. Crear value objects
        PersonalInfo personalInfo = PersonalInfo.of(
                command.firstName(),
                command.lastName(),
                command.phoneNumber()
        );

        Address address = Address.of(
                command.street(),
                command.city(),
                command.state(),
                command.zipCode(),
                Country.valueOf(command.country().toUpperCase())
        );

        Customer customer = Customer.create(personalInfo, email, address);
        Customer savedCustomer = customerRepository.save(customer);

        return savedCustomer.getId();
    }

    @Override
    public Customer execute(String customerId) {
        return null;
    }

    @Override
    public void execute(UpdateAddressCommand command) {

    }

    @Override
    public void execute(UpdateCreditLimitCommand command) {

    }
}
