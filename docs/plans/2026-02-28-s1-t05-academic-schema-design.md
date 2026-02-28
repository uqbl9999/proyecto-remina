# S1-T05: Modelo de Datos Académico v1 — Diseño Validado

Fecha: 2026-02-28
Sprint: 1
Ticket: S1-T05

## Decisiones de diseño

| Decisión | Elección | Razón |
|---|---|---|
| `roles` table | Omitida — enum en `users` | Ya funciona, YAGNI |
| `campuses` | Omitida — 1 sede por colegio MVP | Se agrega en sprint futuro si se necesita |
| `periods` | Omitida — campo `academic_year INT` | Tabla de periodos para después |
| `guardian` vs `users` | Entidades separadas con FK nullable | No todos los apoderados usan el app |
| Migración | 1 archivo V4 atómico | Consistencia, fácil rollback |

## Tablas

### Nuevas

| Tabla | Descripción |
|---|---|
| `tenants` | Colegio = tenant. Un registro por institución. |
| `grades` | Grado académico: "1ro Primaria", "5to Secundaria". |
| `sections` | Sección de un grado: A, B, C. |
| `courses` | Curso: Matemática, Comunicación, etc. |
| `students` | Alumno con código único por tenant. |
| `guardians` | Apoderado. FK nullable a `users` si tiene acceso al app. |
| `student_guardians` | Relación alumno-apoderado con tipo (padre, madre, tutor). |
| `enrollments` | Matricula: alumno → sección × año académico. |

### Modificadas

| Tabla | Cambio |
|---|---|
| `users` | Agregar `tenant_id UUID FK tenants` nullable. S1-T06 lo llena. |

## Esquema

```sql
-- tenants
id UUID PK DEFAULT gen_random_uuid()
name         VARCHAR(255) NOT NULL
ruc          VARCHAR(11)  UNIQUE
active       BOOLEAN      NOT NULL DEFAULT TRUE
created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()

-- users (modificar)
+ tenant_id  UUID FK tenants (nullable)

-- grades
id           UUID PK DEFAULT gen_random_uuid()
tenant_id    UUID NOT NULL FK tenants
name         VARCHAR(100) NOT NULL        -- "1ro Primaria"
level        VARCHAR(20)  NOT NULL        -- PRIMARIA | SECUNDARIA
grade_order  INT          NOT NULL        -- 1-6 primaria, 1-5 secundaria
academic_year INT         NOT NULL
UNIQUE (tenant_id, level, grade_order, academic_year)

-- sections
id           UUID PK DEFAULT gen_random_uuid()
tenant_id    UUID NOT NULL FK tenants
grade_id     UUID NOT NULL FK grades
name         VARCHAR(10)  NOT NULL        -- "A", "B", "C"
academic_year INT         NOT NULL
UNIQUE (tenant_id, grade_id, name, academic_year)

-- courses
id           UUID PK DEFAULT gen_random_uuid()
tenant_id    UUID NOT NULL FK tenants
name         VARCHAR(150) NOT NULL        -- "Matemática"
code         VARCHAR(20)                  -- "MAT" (nullable)
academic_year INT         NOT NULL
UNIQUE (tenant_id, code, academic_year)

-- students
id           UUID PK DEFAULT gen_random_uuid()
tenant_id    UUID NOT NULL FK tenants
first_name   VARCHAR(100) NOT NULL
last_name    VARCHAR(100) NOT NULL
code         VARCHAR(50)                  -- código interno del colegio
birth_date   DATE
active       BOOLEAN      NOT NULL DEFAULT TRUE
created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()

-- guardians
id           UUID PK DEFAULT gen_random_uuid()
tenant_id    UUID NOT NULL FK tenants
first_name   VARCHAR(100) NOT NULL
last_name    VARCHAR(100) NOT NULL
dni          VARCHAR(20)
phone        VARCHAR(20)
email        VARCHAR(255)
user_id      UUID FK users (nullable)     -- link al app si tiene cuenta
created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()

-- student_guardians
student_id   UUID NOT NULL FK students
guardian_id  UUID NOT NULL FK guardians
relationship VARCHAR(50)  NOT NULL        -- padre, madre, abuelo, tutor
is_primary   BOOLEAN      NOT NULL DEFAULT FALSE
PK (student_id, guardian_id)

-- enrollments
id           UUID PK DEFAULT gen_random_uuid()
tenant_id    UUID NOT NULL FK tenants
student_id   UUID NOT NULL FK students
section_id   UUID NOT NULL FK sections
academic_year INT         NOT NULL
status       VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE'  -- ACTIVE, WITHDRAWN, TRANSFERRED
created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
UNIQUE (student_id, academic_year)
```

## Índices

```sql
CREATE INDEX ON users(tenant_id);
CREATE INDEX ON students(tenant_id, last_name, first_name);
CREATE INDEX ON students(tenant_id, code);
CREATE INDEX ON grades(tenant_id, academic_year);
CREATE INDEX ON sections(grade_id, academic_year);
CREATE INDEX ON enrollments(section_id, academic_year);
CREATE INDEX ON enrollments(student_id, academic_year);
CREATE INDEX ON student_guardians(guardian_id);
CREATE INDEX ON guardians(user_id) WHERE user_id IS NOT NULL;
```

## Estrategia de migración

- `V4__create_academic_schema.sql` — DDL completo en orden FK
- `docs/sql/rollback-V4.sql` — DROP TABLE en orden inverso (manual)
- Sin entidades JPA en este ticket — solo SQL
- S1-T06 llenará `users.tenant_id` con el tenant demo

## Aplicar en dev

```bash
./mvnw spring-boot:run        # Flyway corre V4 automáticamente
# o desde cero:
docker compose down -v && docker compose up db redis && ./mvnw spring-boot:run
```
