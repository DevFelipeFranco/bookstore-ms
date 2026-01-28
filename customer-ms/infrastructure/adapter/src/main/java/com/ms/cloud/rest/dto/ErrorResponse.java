package com.ms.cloud.rest.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        String location,
        Map<String, Object> details) {
}
