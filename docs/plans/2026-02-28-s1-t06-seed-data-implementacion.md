# S1-T06: Seed Data Demo — Plan de Implementación

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Poblar la BD demo con 1 colegio, 2 grados, 4 secciones, 6 cursos, 120 alumnos ficticios y 1 apoderado vinculado al usuario padre@demo.com.

**Architecture:** Una migración Flyway `V5__seed_academic_data.sql` con UUIDs fijos para entidades clave, `generate_series` para alumnos, y una ventana ROW_NUMBER para asignar secciones. Solo SQL, sin código Java.

**Tech Stack:** Flyway, PostgreSQL 16, `generate_series`, window functions.

---

### Task 1: Crear V5__seed_academic_data.sql y verificar

**Files:**
- Create: `backend/src/main/resources/db/migration/V5__seed_academic_data.sql`

**Step 1: Crear el archivo de migración**

Crear `backend/src/main/resources/db/migration/V5__seed_academic_data.sql` con el siguiente contenido exacto:

```sql
-- =============================================================
-- S1-T06: Seed data demo
-- 1 tenant · 2 grados · 4 secciones · 6 cursos · 120 alumnos
-- =============================================================

-- UUIDs fijos para referencias FK sin CTEs complejos
-- tenant:     aaaaaaaa-0000-0000-0000-000000000001
-- grade 1ro:  aaaaaaaa-0000-0000-0000-000000000002
-- grade 2do:  aaaaaaaa-0000-0000-0000-000000000003
-- section 1A: aaaaaaaa-0000-0000-0000-000000000004
-- section 1B: aaaaaaaa-0000-0000-0000-000000000005
-- section 2A: aaaaaaaa-0000-0000-0000-000000000006
-- section 2B: aaaaaaaa-0000-0000-0000-000000000007

-- -------------------------------------------------------------
-- 1. Tenant demo
-- -------------------------------------------------------------
INSERT INTO tenants (id, name, ruc)
VALUES ('aaaaaaaa-0000-0000-0000-000000000001', 'Colegio San Martín', '20123456789');

-- -------------------------------------------------------------
-- 2. Asignar tenant a usuarios de V2
-- -------------------------------------------------------------
UPDATE users
SET tenant_id = 'aaaaaaaa-0000-0000-0000-000000000001';

-- -------------------------------------------------------------
-- 3. Grados
-- -------------------------------------------------------------
INSERT INTO grades (id, tenant_id, name, level, grade_order, academic_year)
VALUES
    ('aaaaaaaa-0000-0000-0000-000000000002',
     'aaaaaaaa-0000-0000-0000-000000000001',
     '1ro Primaria', 'PRIMARIA', 1, 2026),
    ('aaaaaaaa-0000-0000-0000-000000000003',
     'aaaaaaaa-0000-0000-0000-000000000001',
     '2do Primaria', 'PRIMARIA', 2, 2026);

-- -------------------------------------------------------------
-- 4. Secciones
-- -------------------------------------------------------------
INSERT INTO sections (id, tenant_id, grade_id, name, academic_year)
VALUES
    ('aaaaaaaa-0000-0000-0000-000000000004',
     'aaaaaaaa-0000-0000-0000-000000000001',
     'aaaaaaaa-0000-0000-0000-000000000002', 'A', 2026),
    ('aaaaaaaa-0000-0000-0000-000000000005',
     'aaaaaaaa-0000-0000-0000-000000000001',
     'aaaaaaaa-0000-0000-0000-000000000002', 'B', 2026),
    ('aaaaaaaa-0000-0000-0000-000000000006',
     'aaaaaaaa-0000-0000-0000-000000000001',
     'aaaaaaaa-0000-0000-0000-000000000003', 'A', 2026),
    ('aaaaaaaa-0000-0000-0000-000000000007',
     'aaaaaaaa-0000-0000-0000-000000000001',
     'aaaaaaaa-0000-0000-0000-000000000003', 'B', 2026);

-- -------------------------------------------------------------
-- 5. Cursos (2026)
-- -------------------------------------------------------------
INSERT INTO courses (tenant_id, name, code, academic_year)
VALUES
    ('aaaaaaaa-0000-0000-0000-000000000001', 'Matemática',         'MAT', 2026),
    ('aaaaaaaa-0000-0000-0000-000000000001', 'Comunicación',       'COM', 2026),
    ('aaaaaaaa-0000-0000-0000-000000000001', 'Ciencias Naturales', 'CNA', 2026),
    ('aaaaaaaa-0000-0000-0000-000000000001', 'Personal Social',    'PSO', 2026),
    ('aaaaaaaa-0000-0000-0000-000000000001', 'Inglés',             'ING', 2026),
    ('aaaaaaaa-0000-0000-0000-000000000001', 'Arte',               'ART', 2026);

-- -------------------------------------------------------------
-- 6. Alumnos (120) — generate_series con nombres cíclicos
-- -------------------------------------------------------------
INSERT INTO students (tenant_id, first_name, last_name, code, birth_date)
SELECT
    'aaaaaaaa-0000-0000-0000-000000000001',
    (ARRAY['Ana','Luis','María','Carlos','Rosa',
           'Pedro','Carmen','Jorge','Elena','Diego'])[(n - 1) % 10 + 1],
    (ARRAY['García','López','Martínez','Rodríguez','González',
           'Pérez','Torres','Flores','Ríos','Cruz'])[(n - 1) % 10 + 1],
    'ALU-' || LPAD(n::text, 4, '0'),
    CASE
        WHEN n <= 60 THEN '2019-03-01'::date + ((n * 3) || ' days')::interval
        ELSE              '2018-03-01'::date + ((n * 3) || ' days')::interval
    END
FROM generate_series(1, 120) AS s(n);

-- -------------------------------------------------------------
-- 7. Matrículas — asignar sección por rango de código
-- -------------------------------------------------------------
INSERT INTO enrollments (tenant_id, student_id, section_id, academic_year)
SELECT
    'aaaaaaaa-0000-0000-0000-000000000001',
    s.id,
    CASE ((ROW_NUMBER() OVER (ORDER BY s.code) - 1) / 30)
        WHEN 0 THEN 'aaaaaaaa-0000-0000-0000-000000000004'::uuid  -- 1ro A
        WHEN 1 THEN 'aaaaaaaa-0000-0000-0000-000000000005'::uuid  -- 1ro B
        WHEN 2 THEN 'aaaaaaaa-0000-0000-0000-000000000006'::uuid  -- 2do A
        ELSE        'aaaaaaaa-0000-0000-0000-000000000007'::uuid  -- 2do B
    END,
    2026
FROM students s
WHERE s.tenant_id = 'aaaaaaaa-0000-0000-0000-000000000001';

-- -------------------------------------------------------------
-- 8. Apoderado demo — vinculado a padre@demo.com y ALU-0001
-- -------------------------------------------------------------
INSERT INTO guardians (tenant_id, first_name, last_name, dni, phone, email, user_id)
SELECT
    'aaaaaaaa-0000-0000-0000-000000000001',
    'Juan', 'Pérez', '12345678', '999000001', 'padre@demo.com',
    u.id
FROM users u
WHERE u.email = 'padre@demo.com';

INSERT INTO student_guardians (student_id, guardian_id, relationship, is_primary)
SELECT s.id, g.id, 'PADRE', true
FROM students s, guardians g
WHERE s.code = 'ALU-0001'
  AND g.email = 'padre@demo.com';
```

**Step 2: Verificar que los archivos de migración están en orden**

```bash
ls /Users/bluq/Downloads/plan-colegio/backend/src/main/resources/db/migration/
```

Expected: V1, V2, V3, V4, V5 presentes.

**Step 3: Compilar**

```bash
cd /Users/bluq/Downloads/plan-colegio/backend && ./mvnw compile -q
```

Expected: BUILD SUCCESS

**Step 4: Verificar que Docker db está corriendo**

```bash
docker compose -f /Users/bluq/Downloads/plan-colegio/docker-compose.yml ps db
```

Si no está corriendo:
```bash
docker compose -f /Users/bluq/Downloads/plan-colegio/docker-compose.yml up db -d && sleep 8
```

**Step 5: Aplicar la migración**

```bash
cd /Users/bluq/Downloads/plan-colegio/backend && timeout 45 ./mvnw spring-boot:run 2>&1 | grep -E "(Flyway|Successfully|ERROR|FlywayException|Started Colegio|migrate)" | head -20
```

Expected:
```
Successfully applied 1 migration to schema "public"
Started ColegioappBackendApplication in ...
```

Si hay error SQL, reportar el mensaje exacto y NO hacer commit.

**Step 6: Verificar conteos en la BD**

```bash
docker exec $(docker ps -qf "ancestor=postgres:16-alpine" | head -1) psql -U colegioapp -d colegioapp -c "
SELECT 'tenants'     AS tabla, COUNT(*)::text AS total FROM tenants
UNION ALL SELECT 'grades',      COUNT(*) FROM grades
UNION ALL SELECT 'sections',    COUNT(*) FROM sections
UNION ALL SELECT 'courses',     COUNT(*) FROM courses
UNION ALL SELECT 'students',    COUNT(*) FROM students
UNION ALL SELECT 'enrollments', COUNT(*) FROM enrollments
UNION ALL SELECT 'guardians',   COUNT(*) FROM guardians
UNION ALL SELECT 'student_guardians', COUNT(*) FROM student_guardians
UNION ALL SELECT 'users con tenant', COUNT(*) FROM users WHERE tenant_id IS NOT NULL;"
```

Expected:
```
tenants           | 1
grades            | 2
sections          | 4
courses           | 6
students          | 120
enrollments       | 120
guardians         | 1
student_guardians | 1
users con tenant  | 6
```

**Step 7: Ejecutar suite de tests**

```bash
cd /Users/bluq/Downloads/plan-colegio/backend && ./mvnw test -q 2>&1 | tail -10
```

Expected: BUILD SUCCESS

**Step 8: Commit**

```bash
cd /Users/bluq/Downloads/plan-colegio && \
git add backend/src/main/resources/db/migration/V5__seed_academic_data.sql && \
git commit -m "feat: seed data demo — 1 colegio, 2 grados, 4 secciones, 120 alumnos

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```
