# S1-T03: Autenticación Base — Diseño Validado

Fecha: 2026-02-27
Sprint: 1
Ticket: S1-T03

## Decisiones de diseño

| Componente | Decisión |
|---|---|
| Endpoints | POST /api/auth/login, /refresh, /logout |
| Access token | JWT HS256, 15 min, stateless |
| Refresh token | UUID v4 en Redis, TTL 7 días |
| Revocación | DELETE key en Redis al hacer logout |
| User mínimo | Tabla `users` via Flyway V1 |
| Seguridad | Spring Security stateless + JwtAuthFilter |
| Auditoría | Logs estructurados con prefijo AUTH_ |
| Mapeo | Mappers manuales (sin MapStruct) |

## Flujo de tokens

```
login     → refresh:{uuid} = userId (TTL 7d) en Redis
refresh   → buscar refresh:{uuid} en Redis → nuevo access token
logout    → DEL refresh:{uuid} de Redis
```

## Estructura de paquetes

```
iam/
├── controller/   AuthController.java
├── service/      AuthService.java, JwtService.java, RefreshTokenService.java
├── repository/   UserRepository.java
├── entity/       User.java
├── enums/        Role.java
├── dto/          LoginRequest.java, LoginResponse.java, RefreshRequest.java
├── security/     JwtAuthFilter.java, SecurityConfig.java, UserDetailsServiceImpl.java
└── mapper/       UserMapper.java
shared/
├── exception/    GlobalExceptionHandler.java
└── dto/          ErrorResponse.java
```
