package com.colegioapp.iam.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RefreshTokenServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOps;

    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        refreshTokenService = new RefreshTokenService(redisTemplate, 604800L);
    }

    @Test
    void create_shouldStoreTokenInRedis() {
        UUID userId = UUID.randomUUID();
        String token = refreshTokenService.create(userId);
        assertThat(token).isNotBlank();
        verify(valueOps).set(
            eq("refresh:" + token),
            eq(userId.toString()),
            eq(604800L),
            eq(TimeUnit.SECONDS)
        );
    }

    @Test
    void findUserId_whenTokenExists_shouldReturnUserId() {
        UUID userId = UUID.randomUUID();
        String token = UUID.randomUUID().toString();
        when(valueOps.get("refresh:" + token)).thenReturn(userId.toString());
        Optional<UUID> result = refreshTokenService.findUserId(token);
        assertThat(result).contains(userId);
    }

    @Test
    void findUserId_whenTokenMissing_shouldReturnEmpty() {
        when(valueOps.get(anyString())).thenReturn(null);
        Optional<UUID> result = refreshTokenService.findUserId("nonexistent");
        assertThat(result).isEmpty();
    }

    @Test
    void revoke_shouldDeleteKeyFromRedis() {
        String token = UUID.randomUUID().toString();
        refreshTokenService.revoke(token);
        verify(redisTemplate).delete("refresh:" + token);
    }
}
