package com.nexus.ecommerce.user.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nexus.ecommerce.common.exception.AppException;
import com.nexus.ecommerce.common.exception.ErrorCode;
import com.nexus.ecommerce.security.JwtService;
import com.nexus.ecommerce.user.dto.LoginRequest;
import com.nexus.ecommerce.user.dto.RefreshTokenRequest;
import com.nexus.ecommerce.user.dto.RegisterRequest;
import com.nexus.ecommerce.user.dto.TokenResponse;
import com.nexus.ecommerce.user.dto.UserResponse;
import com.nexus.ecommerce.user.entity.Role;
import com.nexus.ecommerce.user.entity.User;
import com.nexus.ecommerce.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String REFRESH_PREFIX = "refresh:";
    private static final String BLACKLIST_PREFIX = "blacklist:";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final StringRedisTemplate stringRedisTemplate;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMillis;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }

        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .role(Role.ROLE_USER)
                .build();

        User saved = userRepository.save(user);
        return UserResponse.builder()
                .id(saved.getId())
                .email(saved.getEmail())
                .fullName(saved.getFullName())
                .role(saved.getRole().name())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    public TokenResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        } catch (BadCredentialsException ex) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String accessToken = jwtService.generateAccessToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail(), user.getRole().name());

        // Store active refresh token in Redis with TTL
        stringRedisTemplate.opsForValue().set(
                REFRESH_PREFIX + user.getEmail(),
                refreshToken,
                Duration.ofMillis(refreshExpirationMillis));

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(user.getRole().name())
                .build();
    }

    public TokenResponse refreshToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.refreshToken();
        if (!jwtService.isTokenValid(requestRefreshToken)) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        String email = jwtService.extractEmail(requestRefreshToken);
        String storedToken = stringRedisTemplate.opsForValue().get(REFRESH_PREFIX + email);

        if (storedToken == null || !storedToken.equals(requestRefreshToken)) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String newAccessToken = jwtService.generateAccessToken(user.getEmail(), user.getRole().name());
        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(requestRefreshToken)
                .role(user.getRole().name())
                .build();
    }

    public void logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        String accessToken = authHeader.substring(7);
        if (!jwtService.isTokenValid(accessToken)) {
            return;
        }

        String email = jwtService.extractEmail(accessToken);
        long remainingMillis = jwtService.getRemainingValidityMillis(accessToken);

        // 1. Add access token to Redis blacklist for its remaining lifetime
        if (remainingMillis > 0) {
            stringRedisTemplate.opsForValue().set(
                    BLACKLIST_PREFIX + accessToken,
                    "revoked",
                    Duration.ofMillis(remainingMillis));
        }

        // 2. Delete user's refresh token from Redis
        stringRedisTemplate.delete(REFRESH_PREFIX + email);
    }
}
