# Docker Compose Local — Plan de Implementación

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Un solo `docker compose up --build` levanta PostgreSQL, Redis, backend y web. El desarrollador usa solo `docker compose up db redis` y corre backend/web localmente con hot reload.

**Architecture:** `docker-compose.yml` con 4 servicios. Next.js en modo `standalone` para imagen liviana. Variables de entorno en `.env` raíz.

**Rama de trabajo:** `chore/docker-compose-local`

---

### Task 1: Dockerfile y configuración de Next.js

**Files:**
- Modify: `web/next.config.mjs`
- Create: `web/Dockerfile`
- Create: `web/.dockerignore`

**Step 1: Habilitar `output: standalone` en `web/next.config.mjs`**

Reemplazar el contenido con:
```js
/** @type {import('next').NextConfig} */
const nextConfig = {
  output: 'standalone',
}

export default nextConfig
```

**Step 2: Crear `web/Dockerfile`**

```dockerfile
# Stage 1: dependencias
FROM node:20-alpine AS deps
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci

# Stage 2: build
FROM node:20-alpine AS builder
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY . .
RUN npm run build

# Stage 3: runner
FROM node:20-alpine AS runner
WORKDIR /app
ENV NODE_ENV=production
COPY --from=builder /app/.next/standalone ./
COPY --from=builder /app/.next/static ./.next/static
COPY --from=builder /app/public ./public
EXPOSE 3000
CMD ["node", "server.js"]
```

**Step 3: Crear `web/.dockerignore`**

```
node_modules/
.next/
.env
.env.*
*.md
```

**Step 4: Verificar que el build de Next.js sigue pasando**

```bash
cd /Users/bluq/Downloads/plan-colegio/web && npm run build 2>&1 | tail -10
```

Expected: build exitoso, debe aparecer carpeta `.next/standalone` en el output.

**Step 5: Commit**

```bash
cd /Users/bluq/Downloads/plan-colegio
git add web/next.config.mjs web/Dockerfile web/.dockerignore
git commit -m "chore: web Dockerfile multi-stage y output standalone"
```

---

### Task 2: `docker-compose.yml` y `.env.example` raíz

**Files:**
- Create: `docker-compose.yml`
- Create: `.env.example`

**Step 1: Crear `docker-compose.yml` en la raíz**

```yaml
services:
  db:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: ${DB_NAME:-colegioapp}
      POSTGRES_USER: ${DB_USER:-colegioapp}
      POSTGRES_PASSWORD: ${DB_PASSWORD:-colegioapp}
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${DB_USER:-colegioapp}"]
      interval: 10s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      DB_URL: jdbc:postgresql://db:5432/${DB_NAME:-colegioapp}
      DB_USER: ${DB_USER:-colegioapp}
      DB_PASSWORD: ${DB_PASSWORD:-colegioapp}
      REDIS_URL: redis://redis:6379
      JWT_SECRET: ${JWT_SECRET:-local-dev-secret-change-in-production}
    depends_on:
      db:
        condition: service_healthy
      redis:
        condition: service_healthy

  web:
    build:
      context: ./web
      dockerfile: Dockerfile
    ports:
      - "3000:3000"
    environment:
      NEXT_PUBLIC_API_URL: http://localhost:8080
    depends_on:
      - backend

volumes:
  postgres_data:
  redis_data:
```

**Step 2: Crear `.env.example` en la raíz**

```
DB_NAME=colegioapp
DB_USER=colegioapp
DB_PASSWORD=colegioapp
JWT_SECRET=local-dev-secret-change-in-production
```

**Step 3: Agregar `.env` al `.gitignore` raíz si no está ya**

Verificar que `.gitignore` contiene `.env` y `.env.*`. Si no, agregarlo.

**Step 4: Commit**

```bash
cd /Users/bluq/Downloads/plan-colegio
git add docker-compose.yml .env.example
git commit -m "chore: docker-compose con db, redis, backend y web"
```

---

### Task 3: Actualizar README con ambos flujos

**Files:**
- Modify: `README.md`

**Step 1: Reemplazar el contenido de `README.md`**

```markdown
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
```

**Step 2: Commit**

```bash
cd /Users/bluq/Downloads/plan-colegio
git add README.md
git commit -m "chore: README con flujo tester (Docker) y desarrollador (local)"
```

---

### Task 4: Push a main

**Step 1: Merge y push**

```bash
cd /Users/bluq/Downloads/plan-colegio
git checkout main
git merge chore/docker-compose-local --no-ff -m "merge chore: docker-compose local para tester y desarrollador"
git push origin main
```

**Step 2: Verificar estructura final**

```bash
ls /Users/bluq/Downloads/plan-colegio
```

Expected: `.env.example`, `docker-compose.yml`, `render.yaml`, `README.md`, `backend/`, `web/`, `mobile/`, `docs/`.

---

## Notas

- **NEXT_PUBLIC_API_URL en Docker:** La variable se bake en el build. Apunta a `http://localhost:8080` — correcto para el tester que accede desde su navegador local.
- **Backend tarda ~5-8 min en el primer build** por descarga de dependencias Maven. Los siguientes son más rápidos.
- **Flyway sin migraciones:** El backend arranca sin error, Flyway simplemente no tiene nada que migrar todavía.
- **ddl-auto: validate sin entidades:** Hibernate no valida nada sin `@Entity`. No falla.
