package com.nexus.ecommerce.common.exception;

import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nexus.ecommerce.common.dto.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle custom business logic exceptions
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        log.warn("Business exception occurred: [{}] {}", errorCode.getCode(), errorCode.getMessage());
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode.getCode(), errorCode.getMessage()));
    }

    // Handle @Valid request body validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getDefaultMessage() != null ? err.getDefaultMessage() : "Invalid field")
                .collect(Collectors.joining(", "));
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(ErrorCode.VALIDATION_ERROR.getCode(), errorMessage));
    }

    // Fallback handler for unhandled exceptions
    // Fallback handler for unhandled exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
        log.error("Unhandled exception occurred: ", ex);
        // Append ex.getMessage() temporarily to see the exact root cause in response
        return ResponseEntity
                .internalServerError()
                .body(ApiResponse.error(
                        ErrorCode.INTERNAL_ERROR.getCode(),
                        ErrorCode.INTERNAL_ERROR.getMessage() + ": " + ex.getMessage()));
    }
}
