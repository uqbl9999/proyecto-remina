# Sprint 4 - Ejecucion Detallada (Semana 4)

Fecha: 2026-02-21  
Objetivo: generar libretas y reportes academicos en PDF con procesamiento por lotes.

## 1. Meta del Sprint

1. Generar libreta por alumno.
2. Generar lotes por aula.
3. Publicar documentos en storage seguro.
4. Exponer descarga en web y mobile.

## 2. Dependencias de entrada

1. Modulo de notas en verde.
2. Infra de storage activa.

## 3. Tickets de Sprint

### S4-T01: Motor PDF libreta

1. Plantilla v1 por nivel.
2. Inclusiones: datos alumno, notas por curso, promedio, observaciones.

DoD:

1. PDF valido en todos los casos base.

### S4-T02: Generacion asincrona

1. Cola Redis para lotes.
2. Worker Java para jobs.
3. Estados: `PENDIENTE`, `PROCESANDO`, `GENERADO`, `ERROR`.

DoD:

1. Lotes no bloquean API principal.

### S4-T03: Storage y acceso

1. Guardado en bucket por tenant.
2. URL firmada temporal para descarga.

DoD:

1. Control de acceso por rol y tenant.

### S4-T04: API de reportes

1. Solicitar generacion individual/masiva.
2. Consultar estado de job.
3. Obtener enlace de descarga.

DoD:

1. OpenAPI actualizado.

### S4-T05: UI web

1. Panel de generacion por aula.
2. Tabla de estado de jobs.

DoD:

1. Coordinacion puede generar y descargar sin soporte tecnico.

### S4-T06: UI mobile padres

1. Seccion documentos.
2. Visualizacion/descarga de libreta.

DoD:

1. Archivo visible y descargable en app.

### S4-T07: QA

1. Casos de carga alta en lotes.
2. Validacion de datos en PDF.
3. Pruebas de permisos.

DoD:

1. 0 fugas de acceso cross-tenant.

### S4-T08: UAT

1. Validacion de formato con colegio piloto.

DoD:

1. Aprobacion de plantilla v1.

## 4. Plan Diario de Ejecucion

### Dia 1

1. T01.
2. Iniciar T02.

### Dia 2

1. Cerrar T02.
2. T03.

### Dia 3

1. T04.
2. Iniciar T05.

### Dia 4

1. Cerrar T05 y T06.

### Dia 5

1. T07 y T08.
2. Demo sprint.

## 5. Criterios de Exito

1. Libretas generadas por lote sin caidas.
2. Padres pueden consultarlas.
3. Proceso trazable por estado.

## 6. Riesgos y Contencion

1. Jobs pesados.  
Accion: limites por lote y workers escalables.

2. Cambios de formato.  
Accion: versionado de plantilla.

## 7. Salida a Sprint 5

1. Cierre academico visible para usuarios.
2. Base lista para SIAGIE-like.
