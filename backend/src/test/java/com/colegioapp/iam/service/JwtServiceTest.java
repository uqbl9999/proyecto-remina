package com.colegioapp.iam.service;

import com.colegioapp.iam.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(
            "bXktc3VwZXItc2VjcmV0LWtleS1mb3ItY29sZWdpb2FwcC0yMDI2",
            900000L
        );
    }

    @Test
    void generateToken_shouldReturnValidJwt() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateAccessToken(userId, Role.ADMIN);
        assertThat(token).isNotBlank();
    }

    @Test
    void validateToken_withValidToken_shouldReturnUserId() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateAccessToken(userId, Role.ADMIN);
        UUID extracted = jwtService.extractUserId(token);
        assertThat(extracted).isEqualTo(userId);
    }

    @Test
    void validateToken_withExpiredToken_shouldThrow() {
        JwtService shortLived = new JwtService(
            "bXktc3VwZXItc2VjcmV0LWtleS1mb3ItY29sZWdpb2FwcC0yMDI2",
            -1000L
        );
        UUID userId = UUID.randomUUID();
        String token = shortLived.generateAccessToken(userId, Role.ADMIN);
        assertThatThrownBy(() -> shortLived.extractUserId(token))
            .isInstanceOf(Exception.class);
    }
}
