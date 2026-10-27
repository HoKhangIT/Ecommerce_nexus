package com.nexus.ecommerce.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexus.ecommerce.common.dto.ApiResponse;
import com.nexus.ecommerce.security.CustomUserDetails;
import com.nexus.ecommerce.user.dto.UserResponse;
import com.nexus.ecommerce.user.entity.User;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
        return ResponseEntity.ok(ApiResponse.success(response, "Current user profile"));
    }
}
