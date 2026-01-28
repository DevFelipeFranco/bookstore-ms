package com.ms.cloud.rest.exception;

import com.ms.cloud.exception.CustomerDeactivationException;
import com.ms.cloud.exception.CustomerReactivationException;
import com.ms.cloud.exception.InactiveCustomerException;
import com.ms.cloud.exception.InsufficientCreditException;
import com.ms.cloud.exception.InvalidCreditLimitException;
import com.ms.cloud.exception.InvalidCustomerIdException;
import com.ms.cloud.exception.VipPromotionException;
import com.ms.cloud.rest.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex,
            HttpServletRequest request) {
        log.warn("Validation error: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request, ex);
    }

    @ExceptionHandler({
            InvalidCustomerIdException.class,
            InvalidCreditLimitException.class,
            VipPromotionException.class,
            InsufficientCreditException.class,
            CustomerDeactivationException.class,
            CustomerReactivationException.class,
            InactiveCustomerException.class
    })
    public ResponseEntity<ErrorResponse> handleDomainExceptions(RuntimeException ex, HttpServletRequest request) {
        log.warn("Domain error: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request, ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request, ex);
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, HttpServletRequest request,
            Throwable ex) {
        String location = "Unknown";
        if (ex.getStackTrace().length > 0) {
            StackTraceElement element = ex.getStackTrace()[0];
            location = "%s:%d (%s)".formatted(
                    element.getClassName(),
                    element.getLineNumber(),
                    element.getMethodName());
        }

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                location,
                null);
        return new ResponseEntity<>(errorResponse, status);
    }
}
