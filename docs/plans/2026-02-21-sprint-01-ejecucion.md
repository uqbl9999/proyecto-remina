# Sprint 1 - Ejecucion Detallada (Semana 1)

Fecha: 2026-02-21  
Objetivo: base de producto lista para iniciar asistencia/notas en Sprint 2.

## 1. Meta del Sprint

Dejar operativo un entorno staging con:

1. Backend Java listo con auth + roles.
2. Modelo de datos academico inicial con multi-tenant.
3. Frontend web y app movil con login base.
4. QA smoke y criterios de seguridad minimos.
5. Validacion funcional inicial del flujo de colegio.

## 2. Roles y Responsables

1. Tu (Tech Lead): decisiones de arquitectura y merge final.
2. IA Backend: Spring Boot, seguridad, modelos y APIs.
3. IA Frontend Web: base UI y consumo API.
4. IA Mobile: shell Expo con sesion.
5. IA QA: pruebas smoke y permisos por rol.
6. IA DevOps/Data: CI/CD, staging, seeds, migraciones.
7. Apoyo funcional: UAT de formularios y flujo operativo.

## 3. Tickets de Sprint

### S1-T01: Estructura de proyecto y convenciones

1. Crear modulo `backend` (Spring Boot 3, Java 21, Maven o Gradle).
2. Crear modulo `web` (Next.js + TypeScript).
3. Crear modulo `mobile` (Expo).
4. Definir convencion de ramas y PR template.
5. Configurar lint/formato basico.

DoD:

1. Repositorio compila y levanta localmente.
2. README tecnico base con comandos de arranque.

### S1-T02: Infra y entorno staging

1. Provisionar DB PostgreSQL.
2. Provisionar Redis.
3. Desplegar backend y web en staging.
4. Configurar secretos y variables de entorno.
5. Endpoint `/health` y chequeo de disponibilidad.

DoD:

1. URLs de staging accesibles.
2. Healthcheck OK y logs visibles.

### S1-T03: Autenticacion base (Spring Security + JWT)

1. Login con credenciales.
2. Refresh token.
3. Logout y revocacion basica.
4. Registro de sesion y auditoria minima.

DoD:

1. Flujo login-refresh-logout funcional.
2. Casos invalidos devuelven errores controlados.

### S1-T04: Autorizacion por roles (RBAC)

Roles:

1. Promotor.
2. Director.
3. Admin.
4. Docente.
5. Tesoreria.
6. Padre.

Trabajo:

1. Proteger endpoints por rol.
2. Proteger rutas UI por rol.
3. Respuestas 403 consistentes.

DoD:

1. Matriz de permisos implementada.
2. Pruebas de acceso permitido/denegado.

### S1-T05: Modelo de datos academico v1 (PostgreSQL + Flyway)

Tablas minimas:

1. `tenants`.
2. `campuses`.
3. `users`.
4. `roles`.
5. `students`.
6. `guardians`.
7. `grades`.
8. `sections`.
9. `courses`.
10. `enrollments`.

Reglas:

1. `tenant_id` obligatorio en tablas de dominio.
2. Indices por busqueda frecuente.
3. Constraints de integridad referencial.

DoD:

1. Migraciones limpias en entorno nuevo.
2. Script rollback definido para cambios criticos.

### S1-T06: Datos seed y entorno demo

1. Seed de 1 colegio.
2. 2 grados, 4 secciones.
3. 120 alumnos ficticios.
4. Usuarios por rol para pruebas.

DoD:

1. Seed reproducible con un comando.
2. Datos consistentes para demo y QA.

### S1-T07: Frontend web base

1. Pantalla login.
2. Persistencia de sesion.
3. Layout base del panel.
4. Selector de sede/periodo (placeholder funcional).

DoD:

1. Navegacion estable sin errores criticos.
2. Bloqueo de vistas por rol.

### S1-T08: Mobile shell base

1. Login.
2. Home placeholder.
3. Manejo de token.
4. Cierre de sesion.

DoD:

1. App corre en iOS/Android por Expo.
2. Sesion persiste al reabrir.

### S1-T09: QA smoke automatizado

Cobertura minima:

1. Login exitoso por rol.
2. Login invalido.
3. Acceso autorizado.
4. Acceso denegado.

DoD:

1. Suite smoke ejecuta en CI.
2. Reporte de fallos visible.

### S1-T10: Validacion funcional (UAT inicial)

1. Revisar campos de alumno/matricula.
2. Confirmar jerarquia de roles con colegio.
3. Levantar feedback Must/Should/Could.

DoD:

1. Acta de UAT con ajustes priorizados para Sprint 2.

## 4. Plan Diario de Ejecucion

### Dia 1

1. T01 completo.
2. Arranque T02 y T05.

### Dia 2

1. Cerrar T02.
2. Desarrollar T03.
3. Continuar T05.

### Dia 3

1. Cerrar T03 y T05.
2. Ejecutar T04.
3. Iniciar T07 y T08.

### Dia 4

1. Cerrar T07 y T08.
2. Ejecutar T06 y T09.

### Dia 5

1. Ejecutar T10.
2. Demo de sprint.
3. Cierre de metricas y plan Sprint 2.

## 5. Criterios de Exito de Sprint

1. Staging usable por al menos 3 roles.
2. 0 bloqueantes de autenticacion/autorizacion.
3. Migraciones reproducibles sin intervencion manual.
4. Backlog de Sprint 2 validado con feedback real.

## 6. Riesgos del Sprint 1 y Contencion

1. Riesgo: retraso en infraestructura.  
Accion: priorizar T02 desde dia 1 y usar entorno temporal si aplica.

2. Riesgo: sobrecarga en permisos.  
Accion: arrancar con matriz minima por endpoint critico.

3. Riesgo: cambios funcionales tardios.  
Accion: feedback Must/Should/Could y congelamiento semanal.

## 7. Salida directa a Sprint 2

Si Sprint 1 cierra en verde:

1. Sprint 2 inicia asistencia end-to-end.
2. Se mantiene la misma matriz de roles y modelo base.
3. Se suman pruebas E2E del flujo docente.
