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
