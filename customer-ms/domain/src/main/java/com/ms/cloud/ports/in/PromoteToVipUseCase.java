package com.ms.cloud.ports.in;

import com.ms.cloud.ports.in.command.PromoteToVipCommand;

public interface PromoteToVipUseCase {
    void execute(PromoteToVipCommand command);
}
