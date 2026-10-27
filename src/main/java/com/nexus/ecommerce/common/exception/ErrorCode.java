package com.nexus.ecommerce.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INTERNAL_ERROR(5000, "Unexpected internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    VALIDATION_ERROR(4000, "Invalid input data", HttpStatus.BAD_REQUEST),
    USER_ALREADY_EXISTS(4001, "Email is already registered", HttpStatus.CONFLICT),
    INVALID_CREDENTIALS(4002, "Invalid email or password", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(4003, "Token is invalid, expired, or revoked", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND(4004, "User not found", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_FOUND(4005, "Product not found", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND(4006, "Category not found", HttpStatus.NOT_FOUND),
    DUPLICATE_RESOURCE(4007, "Resource already exists (duplicate SKU or slug)", HttpStatus.CONFLICT),
    CART_EMPTY(4008, "Shopping cart is empty", HttpStatus.BAD_REQUEST),
    OUT_OF_STOCK(4009, "Insufficient inventory stock for this product", HttpStatus.CONFLICT);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
