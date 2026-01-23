package com.ms.cloud.ports.in;

import com.ms.cloud.ports.in.command.UpdateAddressCommand;

public interface UpdateAddressUseCase {
    void execute(UpdateAddressCommand command);
}
