# S1-T05: Modelo de Datos Académico — Plan de Implementación

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Crear el esquema académico multi-tenant en PostgreSQL mediante una migración Flyway atómica.

**Architecture:** Solo SQL — sin entidades JPA nuevas. Hibernate `ddl-auto: validate` solo valida entidades mapeadas; las tablas nuevas sin entidad no causan errores. `tenant_id` se agrega a `users` como nullable; S1-T06 lo llenará. Un archivo V4 crea todas las tablas en orden FK correcto.

**Tech Stack:** Flyway, PostgreSQL 16, Docker Compose (db en puerto 5433).

---

### Task 1: Migración V4 — esquema académico completo

**Files:**
- Create: `backend/src/main/resources/db/migration/V4__create_academic_schema.sql`
- Create: `docs/sql/rollback-V4.sql`

**Step 1: Crear el directorio de rollbacks**

```bash
mkdir -p /Users/bluq/Downloads/plan-colegio/docs/sql
```

**Step 2: Crear `V4__create_academic_schema.sql`**

Crear `backend/src/main/resources/db/migration/V4__create_academic_schema.sql` con el siguiente contenido exacto:

```sql
-- tenants: una fila por colegio
CREATE TABLE tenants (
    id         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name       VARCHAR(255) NOT NULL,
    ruc        VARCHAR(11)  UNIQUE,
    active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- agregar tenant_id a users (nullable — S1-T06 lo asigna)
ALTER TABLE users ADD COLUMN tenant_id UUID REFERENCES tenants(id);

-- grados: 1ro Primaria, 2do Primaria … 5to Secundaria
CREATE TABLE grades (
    id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id     UUID        NOT NULL REFERENCES tenants(id),
    name          VARCHAR(100) NOT NULL,
    level         VARCHAR(20)  NOT NULL CHECK (level IN ('PRIMARIA', 'SECUNDARIA')),
    grade_order   INT          NOT NULL,
    academic_year INT          NOT NULL,
    UNIQUE (tenant_id, level, grade_order, academic_year)
);

-- secciones: A, B, C de cada grado
CREATE TABLE sections (
    id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id     UUID        NOT NULL REFERENCES tenants(id),
    grade_id      UUID        NOT NULL REFERENCES grades(id),
    name          VARCHAR(10)  NOT NULL,
    academic_year INT          NOT NULL,
    UNIQUE (tenant_id, grade_id, name, academic_year)
);

-- cursos: Matemática, Comunicación, etc.
CREATE TABLE courses (
    id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id     UUID        NOT NULL REFERENCES tenants(id),
    name          VARCHAR(150) NOT NULL,
    code          VARCHAR(20),
    academic_year INT          NOT NULL,
    UNIQUE (tenant_id, code, academic_year)
);

-- alumnos
CREATE TABLE students (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID        NOT NULL REFERENCES tenants(id),
    first_name  VARCHAR(100) NOT NULL,
    last_name   VARCHAR(100) NOT NULL,
    code        VARCHAR(50),
    birth_date  DATE,
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- código de alumno único por tenant (solo cuando no es null)
CREATE UNIQUE INDEX ON students(tenant_id, code) WHERE code IS NOT NULL;

-- apoderados (pueden o no tener cuenta en el app)
CREATE TABLE guardians (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID        NOT NULL REFERENCES tenants(id),
    first_name  VARCHAR(100) NOT NULL,
    last_name   VARCHAR(100) NOT NULL,
    dni         VARCHAR(20),
    phone       VARCHAR(20),
    email       VARCHAR(255),
    user_id     UUID        REFERENCES users(id),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- relación alumno-apoderado
CREATE TABLE student_guardians (
    student_id   UUID        NOT NULL REFERENCES students(id),
    guardian_id  UUID        NOT NULL REFERENCES guardians(id),
    relationship VARCHAR(50)  NOT NULL,
    is_primary   BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (student_id, guardian_id)
);

-- matrículas: alumno → sección × año
CREATE TABLE enrollments (
    id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id     UUID        NOT NULL REFERENCES tenants(id),
    student_id    UUID        NOT NULL REFERENCES students(id),
    section_id    UUID        NOT NULL REFERENCES sections(id),
    academic_year INT          NOT NULL,
    status        VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    UNIQUE (student_id, academic_year)
);

-- índices de búsqueda frecuente
CREATE INDEX ON users(tenant_id);
CREATE INDEX ON grades(tenant_id, academic_year);
CREATE INDEX ON sections(grade_id, academic_year);
CREATE INDEX ON courses(tenant_id, academic_year);
CREATE INDEX ON students(tenant_id, last_name, first_name);
CREATE INDEX ON guardians(user_id) WHERE user_id IS NOT NULL;
CREATE INDEX ON student_guardians(guardian_id);
CREATE INDEX ON enrollments(section_id, academic_year);
CREATE INDEX ON enrollments(student_id, academic_year);
```

**Step 3: Crear `docs/sql/rollback-V4.sql`**

```sql
-- Rollback manual de V4__create_academic_schema.sql
-- Ejecutar solo en emergencia. Orden inverso de dependencias FK.
DROP TABLE IF EXISTS enrollments;
DROP TABLE IF EXISTS student_guardians;
DROP TABLE IF EXISTS guardians;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS sections;
DROP TABLE IF EXISTS grades;
ALTER TABLE users DROP COLUMN IF EXISTS tenant_id;
DROP TABLE IF EXISTS tenants;
```

**Step 4: Verificar que los archivos existen**

```bash
ls /Users/bluq/Downloads/plan-colegio/backend/src/main/resources/db/migration/
ls /Users/bluq/Downloads/plan-colegio/docs/sql/
```

Expected: ver V1, V2, V3, V4 en migration/ y rollback-V4.sql en docs/sql/

**Step 5: Compilar para detectar errores de Java**

```bash
cd /Users/bluq/Downloads/plan-colegio/backend && ./mvnw compile -q
```

Expected: BUILD SUCCESS (compile no ejecuta Flyway)

**Step 6: Verificar que Docker db está corriendo**

```bash
docker compose -f /Users/bluq/Downloads/plan-colegio/docker-compose.yml ps db
```

Si no está corriendo:
```bash
docker compose -f /Users/bluq/Downloads/plan-colegio/docker-compose.yml up db -d
# Esperar 5 segundos para que PostgreSQL esté listo
sleep 5
```

**Step 7: Aplicar la migración**

```bash
cd /Users/bluq/Downloads/plan-colegio/backend && ./mvnw spring-boot:run 2>&1 | grep -E "(Flyway|Successfully|ERROR|Started|Failed)" | head -20 &
APP_PID=$!
sleep 20
kill $APP_PID 2>/dev/null
wait $APP_PID 2>/dev/null
```

Expected: ver líneas como:
```
Flyway Community Edition ... by Redgate
Successfully applied 1 migration to schema "public" (execution time ...)
Started ColegioappBackendApplication in ...
```

Si hay error de Flyway (SQL incorrecto), aparecerá `FlywayException` con el mensaje exacto. Corregir el SQL y repetir.

**Step 8: Verificar tablas en la BD**

```bash
docker exec -it $(docker ps -qf "name=db") psql -U colegioapp -d colegioapp -c "\dt"
```

Expected: ver las 9 tablas nuevas (tenants, grades, sections, courses, students, guardians, student_guardians, enrollments) más users de V1.

**Step 9: Ejecutar suite de tests**

```bash
cd /Users/bluq/Downloads/plan-colegio/backend && ./mvnw test -q 2>&1 | tail -10
```

Expected: BUILD SUCCESS — los tests existentes no usan DB real, no se ven afectados.

**Step 10: Commit**

```bash
cd /Users/bluq/Downloads/plan-colegio && \
git add backend/src/main/resources/db/migration/V4__create_academic_schema.sql \
        docs/sql/rollback-V4.sql && \
git commit -m "feat: migración V4 esquema académico multi-tenant"
```
