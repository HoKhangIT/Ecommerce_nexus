package com.nexus.ecommerce.common.dto;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude null fields from JSON response
public class ApiResponse<T> {
    private final int code;
    private final String message;
    private final T data;

    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .code(200)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .build();
    }
}
