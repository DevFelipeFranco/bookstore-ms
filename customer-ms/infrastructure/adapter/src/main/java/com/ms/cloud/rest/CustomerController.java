package com.ms.cloud.rest;

import com.ms.cloud.model.Customer;
import com.ms.cloud.model.valueobject.CustomerId;
import com.ms.cloud.ports.in.CreateCustomerUseCase;
import com.ms.cloud.ports.in.GetCustomerUseCase;
import com.ms.cloud.ports.in.PromoteToVipUseCase;
import com.ms.cloud.ports.in.UpdateAddressUseCase;
import com.ms.cloud.ports.in.command.CreateCustomerCommand;
import com.ms.cloud.ports.in.command.PromoteToVipCommand;
import com.ms.cloud.rest.dto.CreateCustomerRequest;
import com.ms.cloud.rest.dto.CustomerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final GetCustomerUseCase getCustomerUseCase;
    private final PromoteToVipUseCase promoteToVipUseCase;

    @PostMapping
    public ResponseEntity<String> createCustomer(@RequestBody CreateCustomerRequest request) {
        CreateCustomerCommand command = new CreateCustomerCommand(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.phoneNumber(),
                request.street(),
                request.city(),
                request.state(),
                request.zipCode(),
                request.country());

        CustomerId id = createCustomerUseCase.execute(command);
        return new ResponseEntity<>(id.value(), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable String id) {
        Customer customer = getCustomerUseCase.execute(id);
        return ResponseEntity.ok(CustomerResponse.from(customer));
    }

    @PostMapping("/{id}/vip")
    public ResponseEntity<Void> promoteToVip(@PathVariable String id) {
        promoteToVipUseCase.execute(new PromoteToVipCommand(id));
        return ResponseEntity.ok().build();
    }
}
