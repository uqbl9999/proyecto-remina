package com.colegioapp.iam.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RefreshTokenService {

    private static final String KEY_PREFIX = "refresh:";

    private final StringRedisTemplate redisTemplate;
    private final long refreshTokenExpirationSeconds;

    public RefreshTokenService(
        StringRedisTemplate redisTemplate,
        @Value("${jwt.refresh-token-expiration-seconds}") long refreshTokenExpirationSeconds
    ) {
        this.redisTemplate = redisTemplate;
        this.refreshTokenExpirationSeconds = refreshTokenExpirationSeconds;
    }

    public String create(UUID userId) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
            KEY_PREFIX + token,
            userId.toString(),
            refreshTokenExpirationSeconds,
            TimeUnit.SECONDS
        );
        return token;
    }

    public Optional<UUID> findUserId(String token) {
        String value = redisTemplate.opsForValue().get(KEY_PREFIX + token);
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(UUID.fromString(value));
    }

    public void revoke(String token) {
        redisTemplate.delete(KEY_PREFIX + token);
    }
}
