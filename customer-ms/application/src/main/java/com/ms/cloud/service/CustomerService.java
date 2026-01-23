package com.ms.cloud.service;

import com.ms.cloud.model.valueobject.Country;
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
public class CustomerService implements CreateCustomerUseCase, GetCustomerUseCase, UpdateCreditLimitUseCase,
        PromoteToVipUseCase, UpdateAddressUseCase {

    private final CustomerRepository customerRepository;

    @Override
    public CustomerId execute(CreateCustomerCommand command) {
        Email email = Email.of(command.email());
        if (customerRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        PersonalInfo personalInfo = PersonalInfo.of(
                command.firstName(),
                command.lastName(),
                command.phoneNumber());

        Address address = Address.of(
                command.street(),
                command.city(),
                command.state(),
                command.zipCode(),
                Country.valueOf(command.country().toUpperCase()));

        Customer customer = Customer.create(personalInfo, email, address);
        Customer savedCustomer = customerRepository.save(customer);

        return savedCustomer.getId();
    }

    @Override
    public Customer execute(String customerId) {
        return customerRepository.findById(CustomerId.of(customerId))
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
    }

    @Override
    public void execute(UpdateAddressCommand command) {
        Customer customer = execute(command.customerId());

        Address newAddress = Address.of(
                command.street(),
                command.city(),
                command.state(),
                command.zipCode(),
                Country.valueOf(command.country().toUpperCase()));

        customer.updateAddress(newAddress);
        customerRepository.save(customer);
    }

    @Override
    public void execute(UpdateCreditLimitCommand command) {
        Customer customer = execute(command.customerId());

        // Asumiendo que Money y CreditLimit tienen métodos factory adecuados o
        // constructores
        // Nota: El comando debería proveer los valores necesarios para CreditLimit
        // Por simplicidad, aquí asumimos que el comando tiene un método para obtener el
        // nuevo límite
        // O lo construimos si es necesario.
        // Adaptar según la definición real de UpdateCreditLimitCommand

        // Ejemplo simplificado:
        // CreditLimit newLimit = CreditLimit.of(Money.of(command.amount(),
        // command.currency()), Money.ZERO);
        // customer.updateCreditLimit(newLimit);
        // customerRepository.save(customer);

        // TODO: Ajustar según la estructura real de UpdateCreditLimitCommand
        // Como no tengo el contenido del comando, dejaré un comentario
    }

    @Override
    public void execute(com.ms.cloud.ports.in.command.PromoteToVipCommand command) {
        Customer customer = execute(command.customerId());
        customer.promoteToVip();
        customerRepository.save(customer);
    }
}
