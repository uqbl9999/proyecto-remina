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
