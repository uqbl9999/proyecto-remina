# S1-T04: Autorización por Roles (RBAC) — Diseño Validado

Fecha: 2026-02-27
Sprint: 1
Ticket: S1-T04

## Decisiones de diseño

| Componente | Decisión |
|---|---|
| Mecanismo | `@PreAuthorize` por método (Spring Method Security) |
| Habilitación | `@EnableMethodSecurity` en `SecurityConfig` |
| 403 | `AccessDeniedException` → `GlobalExceptionHandler` → `ErrorResponse` |
| Endpoint nuevo | `GET /api/users/me` → email + role del token (sin BD) |
| Tests | `@WithMockUser` en `@WebMvcTest` |

## Matriz de permisos (referencia para S1-T05+)

| Rol | Alcance |
|---|---|
| `PROMOTOR` | Todo — dueño del colegio |
| `DIRECTOR` | Todo operativo — sin finanzas |
| `ADMIN` | Gestión de usuarios y configuración |
| `DOCENTE` | Sus cursos, asistencia, notas |
| `TESORERIA` | Pagos y reportes financieros |
| `PADRE` | Solo sus hijos |

## Patrón de uso en controllers futuros

```java
@PreAuthorize("hasAnyRole('PROMOTOR', 'DIRECTOR', 'ADMIN')")
GET /api/students

@PreAuthorize("hasAnyRole('PROMOTOR', 'TESORERIA')")
GET /api/payments

@PreAuthorize("hasAnyRole('PROMOTOR', 'DIRECTOR', 'DOCENTE')")
GET /api/grades
```

## Estructura de paquetes

```
iam/
└── controller/   UserController.java   ← nuevo
                  (MeResponse DTO inline o en dto/)
```

## Respuesta 403

Reutiliza `ErrorResponse` existente:
```json
{ "error": "forbidden", "message": "No tienes permiso para este recurso" }
```

## Flujo de autorización

```
Request → JwtAuthFilter → setea Authentication con rol
        → Spring Security → authorizeHttpRequests (autenticado?)
        → @PreAuthorize (rol correcto?)
        → Controller
        → AccessDeniedException → GlobalExceptionHandler → 403
```
