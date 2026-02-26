# ColegioApp — Monorepo

SaaS para colegios privados peruanos (200-800 alumnos).

## Prerequisitos

- Docker Desktop 24+
- Java 21+ (solo para desarrollo del backend)
- Node 20+ (solo para desarrollo del web/mobile)

## Módulos

| Módulo | Tecnología | Puerto local |
|--------|-----------|--------------|
| backend | Spring Boot 3 + Java 21 | 8080 |
| web | Next.js 14 | 3000 |
| mobile | Expo | 8081 |
| db | PostgreSQL 16 | 5432 |
| redis | Redis 7 | 6379 |

---

## Arranque para tester (todo con Docker)

```bash
cp .env.example .env
docker compose up --build
```

- Web: http://localhost:3000
- API: http://localhost:8080
- Health: http://localhost:8080/actuator/health

Para detener:
```bash
docker compose down
```

Para detener y borrar datos:
```bash
docker compose down -v
```

---

## Arranque para desarrollador (infra con Docker, código local)

**1. Levantar solo infraestructura:**
```bash
cp .env.example .env
docker compose up db redis
```

**2. Backend (hot reload):**
```bash
cd backend
./mvnw spring-boot:run
```

**3. Web (hot reload):**
```bash
cd web
npm install
npm run dev
```

**4. Mobile:**
```bash
cd mobile
npm install
npx expo start
```

---

## Convención de ramas

```
s{sprint}/t{ticket}-descripcion-corta

# Ejemplos
s1/t01-estructura-proyecto
s1/t03-auth-jwt
s2/t11-asistencia-aula
```

## Variables de entorno

Copiar `.env.example` a `.env` en la raíz antes de correr Docker Compose.
Cada módulo también tiene su propio `.env.example` para desarrollo local sin Docker.
