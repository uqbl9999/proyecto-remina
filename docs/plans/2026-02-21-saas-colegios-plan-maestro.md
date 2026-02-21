# Plan Maestro SaaS para Colegios (Peru)

Fecha: 2026-02-21  
Autor: Fundador + equipo IA

## 1. Resumen Ejecutivo

Se construira un SaaS para colegios privados de primaria-secundaria de 200 a 800 alumnos en Peru.  
El MVP tendra prioridad funcional en este orden:

1. Operacion academica.
2. Cobranza y pagos.
3. Comunicacion colegio-familia.

Ventaja competitiva objetivo:

1. Mejor experiencia movil para padres y docentes.
2. Reduccion de friccion operativa diaria (asistencia, notas, deuda, recordatorios).
3. Flujo practico de compatibilidad con SIAGIE (importacion/exportacion con validaciones).

Horizonte del MVP: 10-12 semanas con salida a piloto real.

## 2. Segmento y Propuesta de Valor

### Segmento inicial

1. Colegios privados de primaria-secundaria medianos (200-800 alumnos).

### Jobs to be done

1. Directivos: control operativo y visibilidad de indicadores.
2. Docentes: asistencia y notas sin retrabajo.
3. Tesoreria: cobranza trazable y menor morosidad.
4. Padres: informacion clara en movil y pagos simples.

### Propuesta de valor MVP

1. Registrar asistencia y notas rapido.
2. Ver libreta y estado de cuenta sin friccion.
3. Pagar pensiones y recibir alertas relevantes.
4. Mantener un puente operativo con SIAGIE via plantillas validadas.

## 3. Alcance del MVP

### Modulo A: Academico (prioridad 1)

1. Gestion base de alumnos, grados, secciones y cursos.
2. Asistencia por aula y fecha.
3. Registro de notas por periodo.
4. Libreta y reportes basicos.
5. Importacion/exportacion tipo SIAGIE con validaciones.

### Modulo B: Cobranza y pagos (prioridad 2)

1. Cronograma de pensiones por alumno o grupo.
2. Estado de cuenta por alumno.
3. Recordatorios automaticos de deuda.
4. Integracion con una pasarela de pago.
5. Conciliacion basica de pagos.

### Modulo C: Comunicacion (prioridad 3)

1. Comunicados segmentados por sede/grado/seccion.
2. Notificaciones push y email.
3. Envio de documentos clave (libreta, estado de cuenta, avisos).

### Fuera de alcance MVP (ir a v2)

1. Facturacion electronica SUNAT construida desde cero.
2. Multiples pasarelas en paralelo.
3. Analitica avanzada predictiva.
4. Marketplace o ecosistema de terceros amplio.

## 4. Arquitectura Tecnica (Backend Java)

## Principios

1. Monolito modular al inicio para velocidad.
2. Multi-tenant desde el dia 1 (`tenant_id` en tablas clave).
3. Seguridad por roles (RBAC).
4. Observabilidad y auditoria desde el inicio.

### Stack recomendado

1. Backend: Java 21 + Spring Boot 3.
2. API: REST JSON (OpenAPI/Swagger).
3. Seguridad: Spring Security + JWT (access/refresh).
4. Datos: PostgreSQL + Flyway para migraciones.
5. Cache/colas: Redis + worker para notificaciones y tareas programadas.
6. Web admin/docente: Next.js + TypeScript.
7. App movil padres/docentes: React Native (Expo).
8. Archivos: S3 compatible.
9. Monitoreo: Sentry + logs estructurados + metricas.

### Modulos del backend

1. `iam`: usuarios, roles, permisos, sesiones.
2. `academic-core`: matricula, asistencia, notas, libretas.
3. `finance-core`: conceptos, cronogramas, deuda, pagos.
4. `communications-core`: comunicados, notificaciones, preferencias.
5. `integrations-siagie`: import/export y validaciones por version.
6. `audit-observability`: auditoria funcional y trazas operativas.

## 5. Modelo Operativo del Equipo

Composicion:

1. Fundador tecnico (tu): arquitectura, priorizacion, merge final.
2. 5 agentes IA: backend, frontend web, mobile, QA, DevOps/Data.
3. 1 apoyo funcional (tu amigo): analisis, UAT, soporte, capacitacion, feedback.

Ritmo semanal:

1. Lunes: plan semanal y congelamiento de alcance semanal.
2. Martes-jueves: construccion por vertical slices.
3. Viernes: demo, UAT, cierre de metricas y decision de release.
4. Sabado: correccion de bugs criticos.

Cadencia diaria:

1. Standup corto.
2. Bloque de construccion.
3. Bloque de pruebas y ajustes.
4. Reporte de cierre del dia.

## 6. Roadmap 12 Semanas

### Fase 1 (Semanas 1-2): Base

1. Arquitectura, datos, auth, roles, staging y seeds.
2. Historias criticas validadas con colegio piloto.

### Fase 2 (Semanas 3-6): Academico

1. Asistencia completa.
2. Notas por periodo y reglas de calculo.
3. Libretas/reportes.
4. Import/export SIAGIE-like con validaciones.

### Fase 3 (Semanas 7-9): Cobranza

1. Cronograma de pensiones.
2. Estado de cuenta.
3. Recordatorios autom.
4. Integracion pasarela + conciliacion basica.

### Fase 4 (Semanas 10-12): Comunicacion + Piloto

1. Comunicados segmentados.
2. Push/email y alertas criticas.
3. Onboarding y capacitacion.
4. Hardening y cierre de piloto.

## 7. Backlog de Producto (Historias y Aceptacion)

### A. Academico

Historia A1: Registrar asistencia por aula.  
Criterios:

1. Marcas por estado (presente/tarde/falta).
2. Guardado seguro y auditoria de cambios.
3. Restricciones por rol para reabrir.

Historia A2: Registrar notas por curso y periodo.  
Criterios:

1. Escalas configurables.
2. Validaciones de rango/formato.
3. Calculo de promedio segun reglas configuradas.

Historia A3: Generar libreta PDF por alumno/aula.  
Criterios:

1. Generacion individual y masiva.
2. Estados de proceso y reintento.
3. Historial de generacion.

Historia A4: Importar/exportar SIAGIE-like.  
Criterios:

1. Validacion por fila/campo.
2. Reporte de errores descargable.
3. Versionado de plantilla.

### B. Cobranza y pagos

Historia B1: Configurar cronograma de pensiones.  
Criterios:

1. Fechas, montos y recargos.
2. Excepciones por alumno.

Historia B2: Pagar desde app/web.  
Criterios:

1. Estado de cuenta actualizado.
2. Flujo idempotente sin doble cargo.
3. Confirmacion de pago visible.

Historia B3: Conciliacion operativa.  
Criterios:

1. Estado por transaccion.
2. Exportable para cierre mensual.

### C. Comunicacion

Historia C1: Enviar comunicados segmentados.  
Criterios:

1. Segmentacion por sede/grado/seccion.
2. Trazabilidad de envio/lectura.

Historia C2: Alertas a padres.  
Criterios:

1. Alertas por ausencia/deuda/comunicado urgente.
2. Historial de notificaciones.

## 8. Riesgos y Mitigaciones

1. Alcance excesivo.  
Mitigacion: regla estricta de alcance MVP y backlog v2.

2. Cambios SIAGIE.  
Mitigacion: modulo desacoplado y plantillas versionadas.

3. Baja adopcion docente/padres.  
Mitigacion: onboarding simple, soporte activo y capacitacion.

4. Complejidad de cobros/fiscal.  
Mitigacion: 1 pasarela + proveedor externo para facturacion.

## 9. KPIs del Piloto

1. Docentes activos semanalmente: >= 70%.
2. Padres activos mensuales: >= 60%.
3. Registro de asistencia diario por aula: >= 85%.
4. Pagos trazables en sistema: >= 80%.
5. Incidentes criticos de produccion: 0.

## 10. Definicion de Hecho (DoD) Global

Cada historia se considera terminada solo si:

1. Codigo mergeado y pipeline en verde.
2. Pruebas minimas pasando (unit/integration/e2e segun aplique).
3. Manejo de errores + logs + auditoria.
4. Validacion funcional UAT.
5. Documentacion operativa corta actualizada.
