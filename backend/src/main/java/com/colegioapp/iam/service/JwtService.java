package com.colegioapp.iam.service;

import com.colegioapp.iam.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final String secret;
    private final long accessTokenExpirationMs;

    public JwtService(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.access-token-expiration-ms}") long accessTokenExpirationMs
    ) {
        this.secret = secret;
        this.accessTokenExpirationMs = accessTokenExpirationMs;
    }

    public String generateAccessToken(UUID userId, Role role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpirationMs);
        return Jwts.builder()
            .subject(userId.toString())
            .claim("role", role.name())
            .issuedAt(now)
            .expiration(expiry)
            .signWith(signingKey())
            .compact();
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(parseClaims(token).getSubject());
    }

    public Role extractRole(String token) {
        return Role.valueOf(parseClaims(token).get("role", String.class));
    }

    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
            .verifyWith(signingKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
