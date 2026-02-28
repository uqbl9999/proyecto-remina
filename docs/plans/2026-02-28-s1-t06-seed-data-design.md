# S1-T06: Seed Data Demo — Diseño Validado

Fecha: 2026-02-28
Sprint: 1
Ticket: S1-T06

## Decisiones de diseño

| Decisión | Elección | Razón |
|---|---|---|
| Formato | Una migración V5 Flyway | Reproducible con un comando |
| UUIDs | Fijos/hardcodeados para entidades clave | Permite referencias FK sin CTEs complejos |
| 120 alumnos | `generate_series(1, 120)` con nombres cíclicos | Simple, reproducible, sin dependencias externas |
| Apoderado | Solo para ALU-0001 vinculado a padre@demo.com | Suficiente para QA del rol PADRE |
| Año académico | 2026 en todos los registros | Año en curso del MVP |

## Datos del seed

### Tenant demo
- Nombre: Colegio San Martín
- RUC: 20123456789
- UUID: `aaaaaaaa-0000-0000-0000-000000000001`

### Grados
| UUID | Nombre | Nivel | Orden |
|---|---|---|---|
| `...0002` | 1ro Primaria | PRIMARIA | 1 |
| `...0003` | 2do Primaria | PRIMARIA | 2 |

### Secciones
| UUID | Grado | Nombre | Alumnos |
|---|---|---|---|
| `...0004` | 1ro Primaria | A | ALU-0001 a ALU-0030 |
| `...0005` | 1ro Primaria | B | ALU-0031 a ALU-0060 |
| `...0006` | 2do Primaria | A | ALU-0061 a ALU-0090 |
| `...0007` | 2do Primaria | B | ALU-0091 a ALU-0120 |

### Cursos (2026)
Matemática, Comunicación, Ciencias Naturales, Personal Social, Inglés, Arte

### Alumnos (120)
- Generados con `generate_series(1, 120)`
- Código: `ALU-0001` … `ALU-0120`
- Primeros nombres (cíclicos): Ana, Luis, María, Carlos, Rosa, Pedro, Carmen, Jorge, Elena, Diego
- Apellidos (cíclicos): García, López, Martínez, Rodríguez, González, Pérez, Torres, Flores, Ríos, Cruz
- Birth date: distribuida según grado (1ro → nacidos 2019, 2do → nacidos 2018)

### Apoderado demo
- Juan Pérez, DNI 12345678, phone 999000001
- user_id → padre@demo.com
- Vinculado a ALU-0001, relación PADRE, is_primary = true

### Actualización de usuarios existentes
- Todos los 6 usuarios de V2 reciben `tenant_id = aaaaaaaa-0000-0000-0000-000000000001`

## Archivo de migración
- `V5__seed_academic_data.sql`

## Verificación
```bash
docker exec <db-container> psql -U colegioapp -d colegioapp -c "
SELECT 'tenants' AS tabla, COUNT(*) FROM tenants
UNION ALL SELECT 'students', COUNT(*) FROM students
UNION ALL SELECT 'enrollments', COUNT(*) FROM enrollments
UNION ALL SELECT 'guardians', COUNT(*) FROM guardians;"
```

Expected: tenants=1, students=120, enrollments=120, guardians=1
