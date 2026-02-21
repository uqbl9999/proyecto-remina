# Sprint 6 - Ejecucion Detallada (Semana 6)

Fecha: 2026-02-21  
Objetivo: estabilizar modulo academico (performance, seguridad, regresion, observabilidad).

## 1. Meta del Sprint

1. Cerrar bugs criticos de S2-S5.
2. Mejorar latencia de endpoints clave.
3. Asegurar permisos y auditoria.
4. Preparar cierre de fase academica.

## 2. Dependencias de entrada

1. S2-S5 desplegados en staging.
2. Reporte de incidencias disponible.

## 3. Tickets de Sprint

### S6-T01: Triage y cierre de bugs criticos

1. Clasificar por severidad y frecuencia.
2. Corregir bloqueantes primero.

DoD:

1. 0 bugs P0/P1 abiertos.

### S6-T02: Optimizacion de consultas

1. Revisar queries lentas.
2. Ajustar indices.
3. Reducir N+1 en servicios Java.

DoD:

1. Endpoints top con mejora medible.

### S6-T03: Seguridad y autorizacion

1. Revisar matriz RBAC por endpoint.
2. Pruebas negativas de acceso.

DoD:

1. Ningun acceso indebido detectado.

### S6-T04: Auditoria ampliada

1. Eventos de asistencia, notas, libretas, SIAGIE-like.
2. Busqueda por usuario/fecha/modulo.

DoD:

1. Trazabilidad completa en acciones criticas.

### S6-T05: Observabilidad

1. Dashboards de errores y latencia.
2. Alertas basicas de salud.

DoD:

1. Alertas activas para fallos criticos.

### S6-T06: QA regresion academica

1. Suite E2E completa del flujo academico.
2. Pruebas de concurrencia basica.

DoD:

1. Regresion en verde.

### S6-T07: UAT de cierre fase academica

1. Demo funcional completa.
2. Acta de conformidad interna.

DoD:

1. Aprobacion para iniciar cobranzas.

## 4. Plan Diario de Ejecucion

### Dia 1

1. T01.

### Dia 2

1. T02.

### Dia 3

1. T03 y T04.

### Dia 4

1. T05.
2. Iniciar T06.

### Dia 5

1. Cerrar T06 y T07.
2. Demo sprint.

## 5. Criterios de Exito

1. Modulo academico estable y medible.
2. Seguridad revisada y aprobada.
3. Regresion completa en verde.

## 6. Riesgos y Contencion

1. Deuda tecnica acumulada.  
Accion: prohibir features nuevas esta semana.

## 7. Salida a Sprint 7

1. Base solida para entrar a modulo de cobranza.
