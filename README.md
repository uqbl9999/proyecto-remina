# ColegioApp — Monorepo

SaaS para colegios privados peruanos (200-800 alumnos).

## Prerequisitos

- Java 21+
- Node 20+
- Docker (para PostgreSQL y Redis local)

## Módulos

| Módulo | Stack | Puerto |
|--------|-------|--------|
| backend | Spring Boot 3 + Java 21 | 8080 |
| web | Next.js 14 | 3000 |
| mobile | Expo | 8081 |

## Arranque local

### Backend

```bash
cd backend && ./mvnw spring-boot:run
```

### Web

```bash
cd web && npm install && npm run dev
```

### Mobile

```bash
cd mobile && npm install && npx expo start
```

## Convención de ramas

Formato: `s{sprint}/t{ticket}-descripcion-corta`

Ejemplos:
- `s1/t01-estructura-proyecto`
- `s1/t03-auth-jwt`
- `s2/t11-asistencia-aula`

## Variables de entorno

Cada módulo tiene `.env.example` con las variables requeridas.
