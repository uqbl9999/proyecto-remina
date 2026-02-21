# Sprint 7 - Ejecucion Detallada (Semana 7)

Fecha: 2026-02-21  
Objetivo: construir base de cobranzas (cronograma, deuda y estado de cuenta).

## 1. Meta del Sprint

1. Configurar conceptos de cobro.
2. Generar cronogramas por alumno o grupo.
3. Calcular estado de cuenta actualizado.

## 2. Dependencias de entrada

1. Fase academica estable (S6).
2. RBAC y auditoria activos.

## 3. Tickets de Sprint

### S7-T01: Modelo financiero base

1. Tablas `billing_concepts`, `payment_schedules`, `account_balances`, `charges`.
2. Reglas de recargo y descuento.

DoD:

1. Integridad financiera basica validada.

### S7-T02: API configuracion de cronograma

1. Crear cronograma por grado/seccion/alumno.
2. Excepciones individuales.

DoD:

1. Tesoreria configura sin intervencion tecnica.

### S7-T03: Motor estado de cuenta

1. Calculo de deuda vigente y vencida.
2. Recalculo ante cambios.

DoD:

1. Estado consistente tras cambios en cronograma.

### S7-T04: UI tesoreria

1. Pantalla conceptos y cronogramas.
2. Vista de deuda por alumno.

DoD:

1. Operacion diaria viable desde panel.

### S7-T05: UI padres (web/mobile)

1. Estado de cuenta resumido y detalle por cuota.

DoD:

1. Padre identifica monto, vencimiento y estado.

### S7-T06: QA financiero

1. Casos de mora, exoneracion y reajuste.

DoD:

1. Resultados coinciden con casos esperados.

### S7-T07: UAT

1. Flujo de tesoreria con casos reales.

DoD:

1. Validacion funcional cerrada.

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

### Dia 5

1. T06 y T07.
2. Demo sprint.

## 5. Criterios de Exito

1. Cronogramas configurables por tesoreria.
2. Deuda visible y consistente por alumno.

## 6. Riesgos y Contencion

1. Reglas de cobro complejas por colegio.  
Accion: parametrizacion por tenant.

## 7. Salida a Sprint 8

1. Base lista para recordatorios y gestion de morosidad.
