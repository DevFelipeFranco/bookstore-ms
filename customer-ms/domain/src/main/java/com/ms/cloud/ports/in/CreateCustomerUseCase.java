package com.ms.cloud.ports.in;

import com.ms.cloud.model.valueobject.CustomerId;
import com.ms.cloud.ports.in.command.CreateCustomerCommand;

public interface CreateCustomerUseCase {
    CustomerId execute(CreateCustomerCommand command);

}
