package com.colegioapp.iam.service;

import com.colegioapp.iam.dto.LoginRequest;
import com.colegioapp.iam.dto.LoginResponse;
import com.colegioapp.iam.entity.User;
import com.colegioapp.iam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmailAndActiveTrue(request.email())
            .orElseThrow(() -> {
                log.warn("AUTH_LOGIN_FAIL user={} reason=user_not_found", request.email());
                return new BadCredentialsException("Credenciales invalidas");
            });

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("AUTH_LOGIN_FAIL user={} reason=bad_credentials", request.email());
            throw new BadCredentialsException("Credenciales invalidas");
        }

        String accessToken = jwtService.generateAccessToken(user.getId(), user.getRole());
        String refreshToken = refreshTokenService.create(user.getId());

        log.info("AUTH_LOGIN_OK user={} role={}", user.getEmail(), user.getRole());
        return new LoginResponse(accessToken, refreshToken, 900L);
    }

    public LoginResponse refresh(String refreshToken) {
        UUID userId = refreshTokenService.findUserId(refreshToken)
            .orElseThrow(() -> {
                log.warn("AUTH_REFRESH_FAIL tokenId={} reason=not_found_or_expired", refreshToken);
                return new BadCredentialsException("Refresh token invalido o expirado");
            });

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BadCredentialsException("Usuario no encontrado"));

        String newAccessToken = jwtService.generateAccessToken(user.getId(), user.getRole());
        log.info("AUTH_REFRESH user={} tokenId={}", user.getEmail(), refreshToken);
        return new LoginResponse(newAccessToken, refreshToken, 900L);
    }

    public void logout(String refreshToken) {
        refreshTokenService.revoke(refreshToken);
        log.info("AUTH_LOGOUT tokenId={}", refreshToken);
    }
}
