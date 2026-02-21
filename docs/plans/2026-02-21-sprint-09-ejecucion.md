# Sprint 9 - Ejecucion Detallada (Semana 9)

Fecha: 2026-02-21  
Objetivo: habilitar pagos online y conciliacion basica confiable.

## 1. Meta del Sprint

1. Integrar una pasarela de pagos.
2. Permitir pago desde web/mobile.
3. Registrar confirmaciones por webhook.
4. Conciliar movimientos para tesoreria.

## 2. Dependencias de entrada

1. Modulo de deuda operativo (S7-S8).
2. Credenciales sandbox de pasarela.

## 3. Tickets de Sprint

### S9-T01: Integracion pasarela

1. Cliente API pasarela.
2. Creacion de orden de pago.
3. Recepcion y validacion de webhook.

DoD:

1. Flujo sandbox end-to-end exitoso.

### S9-T02: Flujo de checkout

1. Web: boton pagar desde estado de cuenta.
2. Mobile: inicio de pago desde app.

DoD:

1. Usuario completa pago sin errores de UX.

### S9-T03: Idempotencia y seguridad

1. Clave idempotente por transaccion.
2. Firma/validacion webhook.

DoD:

1. Sin doble aplicacion ante callbacks repetidos.

### S9-T04: Registro financiero

1. Tabla `payment_transactions` con estados.
2. Relacion a cuotas y alumno.

DoD:

1. Trazabilidad completa por transaccion.

### S9-T05: Conciliacion basica

1. Vista tesoreria de pagos exitosos/fallidos/pendientes.
2. Export CSV mensual.

DoD:

1. Cierre operativo mensual posible.

### S9-T06: QA pagos

1. Casos exitosos, fallidos, timeout, duplicados.

DoD:

1. Matriz de riesgo de pagos cubierta.

### S9-T07: UAT tesoreria

1. Validacion de flujo de cobro real.

DoD:

1. Acta funcional aprobada.

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

1. Pago online funcional y trazable.
2. Estado de cuenta se actualiza correctamente.
3. Tesoreria puede conciliar.

## 6. Riesgos y Contencion

1. Callbacks tardios o duplicados.  
Accion: reconciliacion periodica + idempotencia.

## 7. Salida a Sprint 10

1. Cobranza digital lista.
2. Iniciar comunicacion institucional.
