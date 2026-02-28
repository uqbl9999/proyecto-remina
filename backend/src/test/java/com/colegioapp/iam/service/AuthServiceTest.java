package com.colegioapp.iam.service;

import com.colegioapp.iam.dto.LoginRequest;
import com.colegioapp.iam.dto.LoginResponse;
import com.colegioapp.iam.entity.User;
import com.colegioapp.iam.enums.Role;
import com.colegioapp.iam.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_withValidCredentials_shouldReturnTokens() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("admin@demo.com");
        user.setPassword("hashed");
        user.setRole(Role.ADMIN);

        when(userRepository.findByEmailAndActiveTrue("admin@demo.com"))
            .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed")).thenReturn(true);
        when(jwtService.generateAccessToken(any(), any())).thenReturn("access-token");
        when(refreshTokenService.create(any())).thenReturn("refresh-token");

        LoginResponse response = authService.login(new LoginRequest("admin@demo.com", "password123"));

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
    }

    @Test
    void login_withBadPassword_shouldThrow() {
        User user = new User();
        user.setPassword("hashed");
        when(userRepository.findByEmailAndActiveTrue(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("x@x.com", "wrong")))
            .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void login_withUnknownEmail_shouldThrow() {
        when(userRepository.findByEmailAndActiveTrue(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("nope@x.com", "pass")))
            .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void logout_shouldRevokeRefreshToken() {
        authService.logout("some-refresh-token");
        verify(refreshTokenService).revoke("some-refresh-token");
    }
}
