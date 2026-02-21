# Sprint 10 - Ejecucion Detallada (Semana 10)

Fecha: 2026-02-21  
Objetivo: habilitar comunicados segmentados y centro de notificaciones.

## 1. Meta del Sprint

1. Publicar comunicados desde colegio.
2. Entregar mensajes segmentados a familias.
3. Medir lectura y alcance.

## 2. Dependencias de entrada

1. Canales de notificacion activos.
2. Modelo de usuarios y secciones estable.

## 3. Tickets de Sprint

### S10-T01: Modelo de comunicados

1. Tablas `announcements`, `announcement_targets`, `announcement_reads`.
2. Prioridad y categoria.

DoD:

1. Segmentacion por sede/grado/seccion disponible.

### S10-T02: API publicacion

1. Crear, editar, publicar, archivar comunicado.
2. Validar permisos por rol.

DoD:

1. Flujo auditado por usuario.

### S10-T03: Distribucion de notificaciones

1. Envio push/email segun segmentacion.
2. Reintento y trazabilidad.

DoD:

1. Entrega consistente en pruebas.

### S10-T04: UI panel colegio

1. Editor de comunicado.
2. Selector de audiencia.
3. Vista de metricas de lectura.

DoD:

1. Publicacion operable por personal no tecnico.

### S10-T05: UI web/mobile familias

1. Inbox de comunicados.
2. Marcado de lectura.

DoD:

1. Historial visible y ordenado por fecha/prioridad.

### S10-T06: QA comunicacion

1. Permisos, segmentacion y lectura.
2. Casos de adjuntos.

DoD:

1. Sin filtraciones entre tenants o grupos no destino.

### S10-T07: UAT comunicaciones

1. Escenarios reales de avisos urgentes y regulares.

DoD:

1. Acta UAT aprobada.

## 4. Plan Diario de Ejecucion

### Dia 1

1. T01.

### Dia 2

1. T02.
2. Iniciar T03.

### Dia 3

1. Cerrar T03.
2. T04.

### Dia 4

1. T05.
2. T06.

### Dia 5

1. T07.
2. Demo sprint.

## 5. Criterios de Exito

1. Colegio emite comunicados segmentados correctamente.
2. Familias reciben y leen mensajes.
3. Metricas basicas de alcance disponibles.

## 6. Riesgos y Contencion

1. Exceso de comunicados.  
Accion: categorias y prioridad obligatorias.

## 7. Salida a Sprint 11

1. Canales listos para onboarding del piloto.
