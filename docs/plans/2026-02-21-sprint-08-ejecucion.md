# Sprint 8 - Ejecucion Detallada (Semana 8)

Fecha: 2026-02-21  
Objetivo: automatizar recordatorios y seguimiento de morosidad.

## 1. Meta del Sprint

1. Ejecutar recordatorios por calendario.
2. Personalizar canales de envio.
3. Dar visibilidad de deuda vencida.

## 2. Dependencias de entrada

1. Estado de cuenta operativo (S7).
2. Infra de notificaciones disponible.

## 3. Tickets de Sprint

### S8-T01: Scheduler de cobranzas

1. Jobs diarios para pre-vencimiento y post-vencimiento.
2. Ventanas horarias configurables.

DoD:

1. Jobs corren segun cron definido.

### S8-T02: Plantillas de mensajes

1. Email y push para deuda.
2. Variables dinamicas por alumno/cuota.

DoD:

1. Mensajes salen completos y legibles.

### S8-T03: Preferencias de notificacion

1. Opt-in/opt-out por canal.
2. Respeto de horario permitido.

DoD:

1. Sistema no envia fuera de regla configurada.

### S8-T04: Historial de envios

1. Guardar estado (`ENVIADO`, `FALLIDO`, `REINTENTO`).
2. Vista por alumno y por lote.

DoD:

1. Trazabilidad de envio completa.

### S8-T05: Dashboard morosidad

1. KPIs: vencidos, por vencer, recuperados.
2. Filtros por grado/seccion.

DoD:

1. Tesoreria obtiene lista accionable diaria.

### S8-T06: QA automatizaciones

1. Pruebas de idempotencia.
2. Pruebas de reintento.

DoD:

1. Sin duplicados no deseados.

### S8-T07: UAT de ciclo mensual

1. Simular 30 dias de cobranzas.

DoD:

1. Flujo aprobado por tu amigo.

## 4. Plan Diario de Ejecucion

### Dia 1

1. T01.

### Dia 2

1. T02 y T03.

### Dia 3

1. T04.
2. Iniciar T05.

### Dia 4

1. Cerrar T05.
2. T06.

### Dia 5

1. T07.
2. Demo sprint.

## 5. Criterios de Exito

1. Recordatorios se envian de forma confiable.
2. Tesoreria prioriza morosos con datos claros.

## 6. Riesgos y Contencion

1. Saturacion de mensajes.  
Accion: limite por frecuencia y canal.

## 7. Salida a Sprint 9

1. Entorno listo para cobro en linea.
