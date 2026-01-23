package com.ms.cloud.ports.in;

import com.ms.cloud.ports.in.command.UpdateCreditLimitCommand;

public interface UpdateCreditLimitUseCase {
    void execute(UpdateCreditLimitCommand command);
}
