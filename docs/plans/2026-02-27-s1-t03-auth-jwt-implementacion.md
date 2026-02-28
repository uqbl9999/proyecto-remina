# S1-T03: Autenticación Base (Spring Security + JWT) — Plan de Implementación

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Login con credenciales, refresh token (UUID en Redis), logout con revocación inmediata, auditoría de sesión con logs estructurados.

**Architecture:** Spring Security stateless + JwtAuthFilter. Access token JWT HS256 (15 min). Refresh token UUID almacenado en Redis con TTL 7 días. User mínimo en PostgreSQL via Flyway.

**Tech Stack:** Spring Security, jjwt 0.12.6, spring-boot-starter-data-redis, PostgreSQL, Flyway.

**Rama de trabajo:** `s1/t03-auth-jwt`

---

### Task 1: Rama, dependencia Redis y configuración

**Files:**
- Modify: `backend/pom.xml`
- Modify: `backend/src/main/resources/application.yml`
- Modify: `backend/src/main/resources/application-local.yml`

**Step 1: Crear rama**

```bash
cd /Users/bluq/Downloads/plan-colegio
git checkout main && git checkout -b s1/t03-auth-jwt
```

**Step 2: Agregar `spring-boot-starter-data-redis` al `pom.xml`**

Dentro de `<dependencies>`, antes del cierre `</dependencies>`:

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
```

**Step 3: Actualizar `application.yml` — agregar Redis y JWT config**

Agregar después del bloque `spring.flyway`:

```yaml
  data:
    redis:
      url: ${REDIS_URL:redis://localhost:6379}

jwt:
  secret: ${JWT_SECRET:bXktc3VwZXItc2VjcmV0LWtleS1mb3ItY29sZWdpb2FwcC0yMDI2}
  access-token-expiration-ms: 900000
  refresh-token-expiration-seconds: 604800
```

**Step 4: Actualizar `application-local.yml` — agregar Redis explícito**

El archivo completo debe quedar:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/colegioapp
    username: colegioapp
    password: colegioapp
  data:
    redis:
      url: redis://localhost:6379
```

**Step 5: Verificar que compila**

```bash
cd /Users/bluq/Downloads/plan-colegio/backend && ./mvnw compile -q 2>&1
echo "Exit: $?"
```

Expected: exit code 0.

**Step 6: Commit**

```bash
cd /Users/bluq/Downloads/plan-colegio
git add backend/pom.xml backend/src/main/resources/application.yml backend/src/main/resources/application-local.yml
git commit -m "s1/t03: agregar dependencia Redis y config JWT"
```

---

### Task 2: Migración Flyway V1 y entidad User

**Files:**
- Create: `backend/src/main/resources/db/migration/V1__create_users.sql`
- Create: `backend/src/main/resources/db/migration/V2__seed_users.sql`
- Create: `backend/src/main/java/com/colegioapp/iam/enums/Role.java`
- Create: `backend/src/main/java/com/colegioapp/iam/entity/User.java`
- Create: `backend/src/main/java/com/colegioapp/iam/repository/UserRepository.java`

**Step 1: Crear `V1__create_users.sql`**

```sql
CREATE TABLE users (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    role       VARCHAR(50)  NOT NULL,
    active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
```

**Step 2: Crear `V2__seed_users.sql`**

Passwords son bcrypt de `password123`:

```sql
INSERT INTO users (email, password, role) VALUES
('admin@demo.com',     '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN'),
('director@demo.com',  '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'DIRECTOR'),
('docente@demo.com',   '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'DOCENTE'),
('tesoreria@demo.com', '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'TESORERIA'),
('padre@demo.com',     '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'PADRE'),
('promotor@demo.com',  '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'PROMOTOR');
```

**Step 3: Crear `Role.java`**

```java
package com.colegioapp.iam.enums;

public enum Role {
    PROMOTOR,
    DIRECTOR,
    ADMIN,
    DOCENTE,
    TESORERIA,
    PADRE
}
```

**Step 4: Crear `User.java`**

```java
package com.colegioapp.iam.entity;

import com.colegioapp.iam.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
```

**Step 5: Crear `UserRepository.java`**

```java
package com.colegioapp.iam.repository;

import com.colegioapp.iam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailAndActiveTrue(String email);
}
```

**Step 6: Verificar que compila**

```bash
cd /Users/bluq/Downloads/plan-colegio/backend && ./mvnw compile -q 2>&1
echo "Exit: $?"
```

**Step 7: Commit**

```bash
cd /Users/bluq/Downloads/plan-colegio
git add backend/src/
git commit -m "s1/t03: migracion V1 users, V2 seed, entidad User y Role"
```

---

### Task 3: JwtService

**Files:**
- Create: `backend/src/main/java/com/colegioapp/iam/service/JwtService.java`
- Create: `backend/src/test/java/com/colegioapp/iam/service/JwtServiceTest.java`

**Step 1: Escribir el test primero**

```java
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
```

**Step 2: Correr el test y verificar que falla**

```bash
cd /Users/bluq/Downloads/plan-colegio/backend
./mvnw test -pl . -Dtest=JwtServiceTest -q 2>&1 | tail -15
```

Expected: FAIL — `JwtService` no existe aún.

**Step 3: Implementar `JwtService.java`**

```java
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
```

**Step 4: Correr test y verificar que pasa**

```bash
cd /Users/bluq/Downloads/plan-colegio/backend
./mvnw test -Dtest=JwtServiceTest -q 2>&1 | tail -10
```

Expected: BUILD SUCCESS, 3 tests passed.

**Step 5: Commit**

```bash
cd /Users/bluq/Downloads/plan-colegio
git add backend/src/
git commit -m "s1/t03: JwtService con generacion y validacion de access token"
```

---

### Task 4: RefreshTokenService (Redis)

**Files:**
- Create: `backend/src/main/java/com/colegioapp/iam/service/RefreshTokenService.java`
- Create: `backend/src/test/java/com/colegioapp/iam/service/RefreshTokenServiceTest.java`

**Step 1: Escribir el test primero**

```java
package com.colegioapp.iam.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
```

**Step 2: Correr el test y verificar que falla**

```bash
cd /Users/bluq/Downloads/plan-colegio/backend
./mvnw test -Dtest=RefreshTokenServiceTest -q 2>&1 | tail -10
```

Expected: FAIL — clase no existe.

**Step 3: Implementar `RefreshTokenService.java`**

```java
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
```

**Step 4: Correr test y verificar que pasa**

```bash
cd /Users/bluq/Downloads/plan-colegio/backend
./mvnw test -Dtest=RefreshTokenServiceTest -q 2>&1 | tail -10
```

Expected: BUILD SUCCESS, 4 tests passed.

**Step 5: Commit**

```bash
cd /Users/bluq/Downloads/plan-colegio
git add backend/src/
git commit -m "s1/t03: RefreshTokenService con almacenamiento en Redis"
```

---

### Task 5: AuthService

**Files:**
- Create: `backend/src/main/java/com/colegioapp/iam/service/AuthService.java`
- Create: `backend/src/test/java/com/colegioapp/iam/service/AuthServiceTest.java`

**Step 1: Escribir el test primero**

```java
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
            .isInstanceOf(org.springframework.security.authentication.BadCredentialsException.class);
    }

    @Test
    void login_withUnknownEmail_shouldThrow() {
        when(userRepository.findByEmailAndActiveTrue(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("nope@x.com", "pass")))
            .isInstanceOf(org.springframework.security.authentication.BadCredentialsException.class);
    }

    @Test
    void logout_shouldRevokeRefreshToken() {
        authService.logout("some-refresh-token");
        verify(refreshTokenService).revoke("some-refresh-token");
    }
}
```

**Step 2: Crear DTOs necesarios para el test**

`LoginRequest.java`:
```java
package com.colegioapp.iam.dto;

public record LoginRequest(String email, String password) {}
```

`LoginResponse.java`:
```java
package com.colegioapp.iam.dto;

public record LoginResponse(String accessToken, String refreshToken, long expiresIn) {}
```

`RefreshRequest.java`:
```java
package com.colegioapp.iam.dto;

public record RefreshRequest(String refreshToken) {}
```

**Step 3: Correr el test y verificar que falla**

```bash
cd /Users/bluq/Downloads/plan-colegio/backend
./mvnw test -Dtest=AuthServiceTest -q 2>&1 | tail -10
```

Expected: FAIL — `AuthService` no existe.

**Step 4: Implementar `AuthService.java`**

```java
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
```

**Step 5: Correr test y verificar que pasa**

```bash
cd /Users/bluq/Downloads/plan-colegio/backend
./mvnw test -Dtest=AuthServiceTest -q 2>&1 | tail -10
```

Expected: BUILD SUCCESS, 4 tests passed.

**Step 6: Commit**

```bash
cd /Users/bluq/Downloads/plan-colegio
git add backend/src/
git commit -m "s1/t03: AuthService con login, refresh y logout"
```

---

### Task 6: Spring Security config y JwtAuthFilter

**Files:**
- Create: `backend/src/main/java/com/colegioapp/iam/security/SecurityConfig.java`
- Create: `backend/src/main/java/com/colegioapp/iam/security/JwtAuthFilter.java`
- Create: `backend/src/main/java/com/colegioapp/iam/security/UserDetailsServiceImpl.java`

**Step 1: Crear `UserDetailsServiceImpl.java`**

```java
package com.colegioapp.iam.security;

import com.colegioapp.iam.entity.User;
import com.colegioapp.iam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndActiveTrue(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getId().toString())
            .password(user.getPassword())
            .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
            .build();
    }
}
```

**Step 2: Crear `JwtAuthFilter.java`**

```java
package com.colegioapp.iam.security;

import com.colegioapp.iam.enums.Role;
import com.colegioapp.iam.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtService.isTokenValid(token)) {
                UUID userId = jwtService.extractUserId(token);
                Role role = jwtService.extractRole(token);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + role.name()))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

**Step 3: Crear `SecurityConfig.java`**

```java
package com.colegioapp.iam.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/actuator/health").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

**Step 4: Verificar que compila**

```bash
cd /Users/bluq/Downloads/plan-colegio/backend && ./mvnw compile -q 2>&1
echo "Exit: $?"
```

Expected: exit code 0.

**Step 5: Commit**

```bash
cd /Users/bluq/Downloads/plan-colegio
git add backend/src/
git commit -m "s1/t03: SecurityConfig stateless, JwtAuthFilter y UserDetailsServiceImpl"
```

---

### Task 7: AuthController y manejo de errores

**Files:**
- Create: `backend/src/main/java/com/colegioapp/iam/controller/AuthController.java`
- Create: `backend/src/main/java/com/colegioapp/shared/dto/ErrorResponse.java`
- Create: `backend/src/main/java/com/colegioapp/shared/exception/GlobalExceptionHandler.java`

**Step 1: Crear `ErrorResponse.java`**

```java
package com.colegioapp.shared.dto;

public record ErrorResponse(int status, String error, String message) {}
```

**Step 2: Crear `GlobalExceptionHandler.java`**

```java
package com.colegioapp.shared.exception;

import com.colegioapp.shared.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse(401, "Unauthorized", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Error no controlado: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse(500, "Internal Server Error", "Error interno del servidor"));
    }
}
```

**Step 3: Crear `AuthController.java`**

```java
package com.colegioapp.iam.controller;

import com.colegioapp.iam.dto.LoginRequest;
import com.colegioapp.iam.dto.LoginResponse;
import com.colegioapp.iam.dto.RefreshRequest;
import com.colegioapp.iam.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }
}
```

**Step 4: Verificar que compila**

```bash
cd /Users/bluq/Downloads/plan-colegio/backend && ./mvnw compile -q 2>&1
echo "Exit: $?"
```

**Step 5: Commit**

```bash
cd /Users/bluq/Downloads/plan-colegio
git add backend/src/
git commit -m "s1/t03: AuthController, GlobalExceptionHandler y ErrorResponse"
```

---

### Task 8: Test de integración y verificación final

**Files:**
- Create: `backend/src/test/java/com/colegioapp/iam/controller/AuthControllerTest.java`

**Step 1: Correr todos los tests unitarios**

```bash
cd /Users/bluq/Downloads/plan-colegio/backend
./mvnw test -q 2>&1 | tail -15
```

Expected: todos los tests pasan (JwtServiceTest, RefreshTokenServiceTest, AuthServiceTest).

**Step 2: Crear test de integración del AuthController**

```java
package com.colegioapp.iam.controller;

import com.colegioapp.iam.dto.LoginRequest;
import com.colegioapp.iam.dto.LoginResponse;
import com.colegioapp.iam.dto.RefreshRequest;
import com.colegioapp.iam.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void login_withValidCredentials_returns200() throws Exception {
        LoginResponse response = new LoginResponse("access-token", "refresh-token", 900L);
        when(authService.login(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("admin@demo.com", "password123"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("access-token"))
            .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    void login_withInvalidCredentials_returns401() throws Exception {
        when(authService.login(any())).thenThrow(new BadCredentialsException("Credenciales invalidas"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("x@x.com", "wrong"))))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void logout_returns204() throws Exception {
        doNothing().when(authService).logout(any());

        mockMvc.perform(post("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RefreshRequest("some-token"))))
            .andExpect(status().isNoContent());
    }
}
```

**Step 3: Correr todos los tests**

```bash
cd /Users/bluq/Downloads/plan-colegio/backend
./mvnw test -q 2>&1 | tail -15
```

Expected: BUILD SUCCESS, todos los tests pasan.

**Step 4: Commit**

```bash
cd /Users/bluq/Downloads/plan-colegio
git add backend/src/
git commit -m "s1/t03: test de integracion AuthController"
```

---

### Task 9: Push a main y verificación

**Step 1: Merge y push**

```bash
cd /Users/bluq/Downloads/plan-colegio
git checkout main
git merge s1/t03-auth-jwt --no-ff -m "merge s1/t03: autenticacion base Spring Security + JWT"
git push origin main
```

**Step 2: Verificar arranque con DB y Redis corriendo**

Con `docker compose up db redis` activo:

```bash
cd /Users/bluq/Downloads/plan-colegio/backend && ./mvnw spring-boot:run 2>&1 | grep -E "(Started|ERROR|WARN)" | head -10
```

Expected: `Started ColegioappBackendApplication`

**Step 3: Smoke test manual**

```bash
# Login exitoso
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@demo.com","password":"password123"}' | jq .

# Login fallido
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@demo.com","password":"wrong"}' | jq .
```

Expected:
- Login OK → 200 con `accessToken` y `refreshToken`
- Login fallido → 401 con `{"status":401,"error":"Unauthorized",...}`

---

## Notas para el agente ejecutor

- **Checkstyle**: Todos los archivos Java deben usar 4 espacios (no tabs), líneas ≤ 120 chars, imports explícitos sin asterisco. Si Checkstyle falla, corregir antes del commit.
- **`@WebMvcTest` con Security**: El test de AuthController puede fallar si Spring Security no está configurado para tests. Si hay error 403, agregar `@Import(SecurityConfig.class)` o `@WithMockUser` según corresponda. Ajustar según el error real.
- **Orden de migraciones**: V1 crea la tabla, V2 inserta seed. Si hay migraciones anteriores que fallen, verificar el estado de Flyway con `./mvnw flyway:info`.
- **JWT secret en tests**: El secret del test es el mismo del `application.yml` default. No se necesita configuración extra para tests unitarios de `JwtService`.
- **`ddl-auto: validate`**: Con la migración V1, Hibernate validará que la tabla `users` existe. Si la DB ya tiene datos de pruebas anteriores sin la tabla, hacer `docker compose down -v && docker compose up db redis`.
