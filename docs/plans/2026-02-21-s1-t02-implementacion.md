# S1-T02: Infra y Entorno Staging — Plan de Implementación

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Dejar PostgreSQL, Redis, backend y web desplegados en Render con el endpoint `/actuator/health` respondiendo OK.

**Architecture:** 4 servicios en Render free tier definidos via `render.yaml` (IaC). Backend como Docker container multi-stage. Web como Node Web Service. Auto-deploy en push a `main`.

**Tech Stack:** Render, Docker, Spring Boot Actuator, eclipse-temurin:21-alpine.

**Rama de trabajo:** `s1/t02-infra-staging`

---

### Task 1: Rama y actualización del backend para staging

**Files:**
- Modify: `backend/pom.xml`
- Modify: `backend/src/main/resources/application.yml`
- Modify: `backend/.env.example`

**Step 1: Crear rama**

```bash
git checkout main && git checkout -b s1/t02-infra-staging
```

**Step 2: Agregar Spring Boot Actuator al `pom.xml`**

Dentro de `<dependencies>`, agregar antes del cierre `</dependencies>`:

```xml
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
```

**Step 3: Actualizar `backend/src/main/resources/application.yml`**

Reemplazar el bloque `spring.datasource` y agregar sección `management`:

```yaml
spring:
  application:
    name: colegioapp-backend
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:colegioapp}
    username: ${DB_USER:colegioapp}
    password: ${DB_PASSWORD:colegioapp}
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  flyway:
    enabled: true
    locations: classpath:db/migration

server:
  port: 8080

management:
  endpoints:
    web:
      exposure:
        include: health
  endpoint:
    health:
      show-details: always

logging:
  level:
    com.colegioapp: INFO
```

**Step 4: Actualizar `backend/.env.example`**

```
DB_HOST=localhost
DB_PORT=5432
DB_NAME=colegioapp
DB_USER=colegioapp
DB_PASSWORD=colegioapp
JWT_SECRET=cambia-esto-por-un-secreto-de-256-bits
REDIS_URL=redis://localhost:6379
```

**Step 5: Verificar que el backend compila**

```bash
cd /Users/bluq/Downloads/plan-colegio/backend && ./mvnw compile -q 2>&1
echo "Exit: $?"
```

Expected: exit code 0, BUILD SUCCESS.

**Step 6: Commit**

```bash
cd /Users/bluq/Downloads/plan-colegio
git add backend/pom.xml backend/src/main/resources/application.yml backend/.env.example
git commit -m "s1/t02: agregar actuator, vars de entorno por componente"
```

---

### Task 2: Dockerfile multi-stage para el backend

**Files:**
- Create: `backend/Dockerfile`
- Create: `backend/.dockerignore`

**Step 1: Crear `backend/Dockerfile`**

```dockerfile
# Stage 1: build
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -q
COPY src/ src/
RUN ./mvnw package -DskipTests -q

# Stage 2: runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Step 2: Crear `backend/.dockerignore`**

```
target/
.mvn/wrapper/maven-wrapper.jar
*.md
.env
.env.*
```

**Step 3: Verificar que Docker build pasa localmente**

Requiere Docker Desktop corriendo. Si no está disponible, saltar este step y confiar en el build de Render.

```bash
cd /Users/bluq/Downloads/plan-colegio/backend
docker build -t colegioapp-backend:local . 2>&1 | tail -5
```

Expected: `Successfully built <id>` o similar.

**Step 4: Commit**

```bash
cd /Users/bluq/Downloads/plan-colegio
git add backend/Dockerfile backend/.dockerignore
git commit -m "s1/t02: Dockerfile multi-stage Java 21 alpine"
```

---

### Task 3: render.yaml en la raíz del repo

**Files:**
- Create: `render.yaml`

**Step 1: Crear `render.yaml` en la raíz**

```yaml
services:
  - type: web
    name: colegioapp-backend
    runtime: docker
    dockerfilePath: ./backend/Dockerfile
    dockerContext: ./backend
    healthCheckPath: /actuator/health
    envVars:
      - key: DB_HOST
        fromDatabase:
          name: colegioapp-db
          property: host
      - key: DB_PORT
        fromDatabase:
          name: colegioapp-db
          property: port
      - key: DB_NAME
        fromDatabase:
          name: colegioapp-db
          property: database
      - key: DB_USER
        fromDatabase:
          name: colegioapp-db
          property: user
      - key: DB_PASSWORD
        fromDatabase:
          name: colegioapp-db
          property: password
      - key: REDIS_URL
        fromService:
          type: redis
          name: colegioapp-redis
          property: connectionString
      - key: JWT_SECRET
        generateValue: true

  - type: web
    name: colegioapp-web
    runtime: node
    rootDir: web
    buildCommand: npm install && npm run build
    startCommand: npm start
    envVars:
      - key: NEXT_PUBLIC_API_URL
        value: https://colegioapp-backend.onrender.com

  - type: redis
    name: colegioapp-redis
    plan: free
    ipAllowList: []

databases:
  - name: colegioapp-db
    databaseName: colegioapp
    user: colegioapp
    plan: free
```

**NOTA:** El valor de `NEXT_PUBLIC_API_URL` (`https://colegioapp-backend.onrender.com`) es el nombre que Render asigna por defecto. Si el nombre ya está tomado en Render, el servicio recibirá un sufijo aleatorio — en ese caso ajustar este valor desde el dashboard de Render después del primer deploy.

**Step 2: Commit**

```bash
cd /Users/bluq/Downloads/plan-colegio
git add render.yaml
git commit -m "s1/t02: render.yaml con los 4 servicios de staging"
```

---

### Task 4: Push a main y despliegue en Render

**Step 1: Push a main**

```bash
cd /Users/bluq/Downloads/plan-colegio
git checkout main
git merge s1/t02-infra-staging --no-ff -m "merge s1/t02: infra y entorno staging"
git push origin main
```

**Step 2: Crear Blueprint en Render (manual — requiere browser)**

1. Ir a [https://dashboard.render.com](https://dashboard.render.com)
2. Click **"New +"** → **"Blueprint"**
3. Conectar el repo `uqbl9999/proyecto-remina`
4. Render detecta el `render.yaml` automáticamente
5. Click **"Apply"** — Render crea los 4 servicios en orden:
   - Primero: `colegioapp-db` (PostgreSQL) y `colegioapp-redis`
   - Luego: `colegioapp-backend` (Docker build, ~5-8 min)
   - Luego: `colegioapp-web` (Node build, ~2-3 min)

**Step 3: Monitorear el deploy del backend**

En el dashboard de Render, ir a `colegioapp-backend` → **"Logs"**. Esperar ver:

```
Started ColegioappBackendApplication in X.XXX seconds
```

**Step 4: Verificar healthcheck**

```bash
curl https://colegioapp-backend.onrender.com/actuator/health
```

Expected:
```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "diskSpace": { "status": "UP" },
    "ping": { "status": "UP" },
    "redis": { "status": "UP" }
  }
}
```

Si `db` o `redis` están en `DOWN`, revisar los logs del backend en Render para ver el error de conexión.

**Step 5: Verificar web**

Abrir en browser: `https://colegioapp-web.onrender.com`

Expected: página de Next.js cargando sin errores (la pantalla default de Next.js es suficiente para el DoD de este ticket).

---

## Notas para el agente ejecutor

- **Flyway sin migraciones:** En este ticket no hay archivos en `db/migration/`. Flyway arrancará sin problema — simplemente no tendrá nada que migrar. El error vendría si `flyway.enabled=true` y el schema ya tiene tablas que Flyway no reconoce, lo cual no ocurre en una DB nueva.
- **ddl-auto: validate con cero entidades:** Hibernate no valida nada si no hay `@Entity` mapeadas. No fallará.
- **Primer deploy lento:** El Docker build en Render tarda 5-8 min la primera vez (descarga imagen base + dependencias Maven). Los siguientes son más rápidos por caché.
- **NEXT_PUBLIC_API_URL:** Esta variable se bake en el build de Next.js. Si el nombre del backend en Render tiene sufijo aleatorio, hay que actualizar la env var en el dashboard de Render y re-deployar el web.
- **Free tier sleep:** Los servicios web duermen tras 15 min sin tráfico. El healthcheck de Render los mantiene despiertos mientras están activos.
