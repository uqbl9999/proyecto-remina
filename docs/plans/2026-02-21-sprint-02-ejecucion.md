# Sprint 2 - Ejecucion Detallada (Semana 2)

Fecha: 2026-02-21  
Objetivo: implementar asistencia end-to-end para web y mobile con auditoria y permisos.

## 1. Meta del Sprint

Entregar flujo completo de asistencia:

1. Configuracion de sesion academica (fecha, aula, curso).
2. Registro de asistencia por alumno.
3. Cierre y reapertura controlada por rol.
4. Consulta de resumen por coordinacion.
5. Trazabilidad de cambios.

## 2. Dependencias de entrada

1. Auth y RBAC del Sprint 1 en verde.
2. Modelo base de alumnos/secciones activo.
3. Staging operativo con CI/CD.

## 3. Tickets de Sprint

### S2-T01: Modelo de asistencia y migraciones

1. Crear tablas `attendance_sessions` y `attendance_records`.
2. Definir constraint unico por `tenant_id + student_id + course_id + date`.
3. Estados permitidos: `PRESENTE`, `TARDE`, `FALTA`, `JUSTIFICADO`.

DoD:

1. Migraciones Flyway aplican sin errores.
2. Integridad referencial completa.

### S2-T02: API asistencia docente

1. Endpoint crear sesion de asistencia.
2. Endpoint guardar asistencias por lote.
3. Endpoint cierre de sesion.
4. Endpoint reapertura con validacion de rol.

DoD:

1. API documentada en OpenAPI.
2. Respuestas de error consistentes.

### S2-T03: Reglas de negocio

1. No permitir duplicidad por alumno-fecha-curso.
2. No permitir edicion tras cierre (excepto roles autorizados).
3. Registrar motivo de reapertura.

DoD:

1. Reglas cubiertas por pruebas unitarias.
2. Eventos invalidos retornan 4xx con mensaje claro.

### S2-T04: Auditoria de asistencia

1. Guardar `before/after` en cambios de estado.
2. Guardar usuario, fecha y origen (web/mobile).
3. Endpoint de historial para coordinacion.

DoD:

1. Historial visible por registro.
2. Eventos criticos trazados.

### S2-T05: UI web docente

1. Vista lista de alumnos por seccion.
2. Atajos de marcado rapido.
3. Guardado parcial y estado visual.

DoD:

1. Flujo completo en <= 5 minutos por aula de 30 alumnos.
2. Manejo de errores de red sin perdida de trabajo.

### S2-T06: UI web coordinador

1. Resumen diario por seccion.
2. Filtro por fecha, grado, curso.
3. Vista de pendientes de registro.

DoD:

1. Coordinador identifica secciones faltantes del dia.
2. Export basico CSV de resumen diario.

### S2-T07: Mobile docente (basico)

1. Login y seleccion de aula.
2. Marcado de asistencia por alumno.
3. Sincronizacion segura con API.

DoD:

1. Registro funcional en iOS y Android via Expo.
2. Reintento si falla la red.

### S2-T08: QA y UAT

1. Unit tests de reglas de asistencia.
2. Integration tests de API.
3. E2E web docente.
4. UAT con checklist funcional.

DoD:

1. Pipeline CI verde.
2. Acta de UAT con ajustes priorizados.

## 4. Plan Diario de Ejecucion

### Dia 1

1. T01 completo.
2. Iniciar T02.

### Dia 2

1. Cerrar T02.
2. Ejecutar T03.
3. Iniciar T04.

### Dia 3

1. Cerrar T04.
2. Ejecutar T05.

### Dia 4

1. Ejecutar T06 y T07.
2. Integracion de flujos.

### Dia 5

1. Ejecutar T08.
2. Demo y cierre de sprint.

## 5. Criterios de Exito

1. Asistencia registrada por docente sin bloqueos.
2. Coordinador ve estado diario consolidado.
3. Auditoria disponible en cambios clave.
4. 0 bugs bloqueantes abiertos.

## 6. Riesgos y Contencion

1. Listas de alumnos lentas.  
Accion: paginacion o carga incremental.

2. Errores de red en mobile.  
Accion: cola local y reintento controlado.

## 7. Salida a Sprint 3

1. Dataset de asistencia estable.
2. Base lista para iniciar modulo de notas.
