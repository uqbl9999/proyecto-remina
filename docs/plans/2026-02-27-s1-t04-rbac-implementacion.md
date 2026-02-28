# S1-T04: RBAC — Plan de Implementación

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Habilitar autorización por roles en el backend con `@PreAuthorize`, respuestas 403 consistentes y endpoint `GET /api/users/me`.

**Architecture:** Se agrega `@EnableMethodSecurity` a `SecurityConfig` para activar `@PreAuthorize`. `GlobalExceptionHandler` maneja `AccessDeniedException` → 403 con `ErrorResponse`. `UserController` expone `/api/users/me` leyendo el `Authentication` del contexto (sin tocar la BD).

**Tech Stack:** Spring Security 6 Method Security, `@PreAuthorize`, `@WithMockUser` (spring-security-test), `@WebMvcTest`.

---

### Task 1: Habilitar infraestructura RBAC

**Files:**
- Modify: `backend/src/main/java/com/colegioapp/iam/security/SecurityConfig.java`
- Modify: `backend/src/main/java/com/colegioapp/shared/exception/GlobalExceptionHandler.java`

**Step 1: Agregar `@EnableMethodSecurity` a SecurityConfig**

En `SecurityConfig.java`, agregar la anotación después de `@EnableWebSecurity`:

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
```

Import a agregar:
```java
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
```

**Step 2: Agregar handler de AccessDeniedException en GlobalExceptionHandler**

Agregar el método entre el handler de `BadCredentialsException` y el handler general de `Exception`:

```java
@ExceptionHandler(AccessDeniedException.class)
public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new ErrorResponse(403, "Forbidden", "No tienes permiso para este recurso"));
}
```

Imports a agregar:
```java
import org.springframework.security.access.AccessDeniedException;
```

El archivo completo queda así:

```java
package com.colegioapp.shared.exception;

import com.colegioapp.shared.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new ErrorResponse(403, "Forbidden", "No tienes permiso para este recurso"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Error no controlado: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse(500, "Internal Server Error", "Error interno del servidor"));
    }
}
```

**Step 3: Verificar que compila**

```bash
cd backend && ./mvnw compile -q
```

Expected: BUILD SUCCESS

**Step 4: Commit**

```bash
git add backend/src/main/java/com/colegioapp/iam/security/SecurityConfig.java \
        backend/src/main/java/com/colegioapp/shared/exception/GlobalExceptionHandler.java
git commit -m "feat: habilitar @EnableMethodSecurity y manejo 403"
```

---

### Task 2: TDD — GET /api/users/me + validación RBAC

**Files:**
- Create: `backend/src/test/java/com/colegioapp/iam/controller/UserControllerTest.java`
- Create: `backend/src/main/java/com/colegioapp/iam/dto/MeResponse.java`
- Create: `backend/src/main/java/com/colegioapp/iam/controller/UserController.java`

**Step 1: Escribir el test**

Crear `UserControllerTest.java`:

```java
package com.colegioapp.iam.controller;

import com.colegioapp.iam.security.JwtAuthFilter;
import com.colegioapp.iam.security.SecurityConfig;
import com.colegioapp.iam.service.JwtService;
import com.colegioapp.shared.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UserController.class, UserControllerTest.SecuredStub.class})
@Import({SecurityConfig.class, JwtAuthFilter.class, GlobalExceptionHandler.class})
class UserControllerTest {

    @RestController
    @RequestMapping("/api/test")
    static class SecuredStub {

        @GetMapping("/admin-only")
        @PreAuthorize("hasRole('ADMIN')")
        public String adminOnly() {
            return "ok";
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000", roles = "ADMIN")
    void me_withAuthenticatedUser_returns200() throws Exception {
        mockMvc.perform(get("/api/users/me"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value("123e4567-e89b-12d3-a456-426614174000"))
            .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @WithMockUser(roles = "DOCENTE")
    void adminOnly_withDocente_returns403() throws Exception {
        mockMvc.perform(get("/api/test/admin-only"))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.status").value(403))
            .andExpect(jsonPath("$.error").value("Forbidden"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminOnly_withAdmin_returns200() throws Exception {
        mockMvc.perform(get("/api/test/admin-only"))
            .andExpect(status().isOk());
    }
}
```

**Step 2: Ejecutar el test para verificar que falla**

```bash
cd backend && ./mvnw test -pl . -Dtest=UserControllerTest -q 2>&1 | tail -20
```

Expected: FAIL — `UserController` no existe aún.

**Step 3: Crear MeResponse**

Crear `backend/src/main/java/com/colegioapp/iam/dto/MeResponse.java`:

```java
package com.colegioapp.iam.dto;

public record MeResponse(String userId, String role) { }
```

**Step 4: Crear UserController**

Crear `backend/src/main/java/com/colegioapp/iam/controller/UserController.java`:

```java
package com.colegioapp.iam.controller;

import com.colegioapp.iam.dto.MeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(Authentication authentication) {
        String role = authentication.getAuthorities().stream()
            .findFirst()
            .map(a -> a.getAuthority().replace("ROLE_", ""))
            .orElse("UNKNOWN");
        return ResponseEntity.ok(new MeResponse(authentication.getName(), role));
    }
}
```

**Step 5: Ejecutar los tests para verificar que pasan**

```bash
cd backend && ./mvnw test -pl . -Dtest=UserControllerTest -q 2>&1 | tail -20
```

Expected: BUILD SUCCESS, 3 tests passed.

**Step 6: Ejecutar toda la suite**

```bash
cd backend && ./mvnw test -q 2>&1 | tail -20
```

Expected: BUILD SUCCESS, todos los tests pasan.

**Step 7: Commit**

```bash
git add backend/src/main/java/com/colegioapp/iam/dto/MeResponse.java \
        backend/src/main/java/com/colegioapp/iam/controller/UserController.java \
        backend/src/test/java/com/colegioapp/iam/controller/UserControllerTest.java
git commit -m "feat: GET /api/users/me y tests RBAC (permitido/denegado)"
```
