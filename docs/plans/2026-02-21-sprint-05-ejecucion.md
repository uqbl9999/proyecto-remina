# Sprint 5 - Ejecucion Detallada (Semana 5)

Fecha: 2026-02-21  
Objetivo: implementar import/export SIAGIE-like con validaciones y reporte de errores.

## 1. Meta del Sprint

1. Cargar archivos de entrada estandarizados.
2. Validar estructura y contenido por fila.
3. Exportar datos academicos en plantilla versionada.
4. Dar feedback accionable a secretaria/coordinacion.

## 2. Dependencias de entrada

1. Datos academicos estables S2-S4.
2. Storage activo para archivos.

## 3. Tickets de Sprint

### S5-T01: Parser y validador de importacion

1. Lectura de XLSX/CSV.
2. Validaciones de encabezados y tipos.
3. Validaciones de reglas academicas.

DoD:

1. Mensajes de error por fila/campo.

### S5-T02: API importacion

1. Subida de archivo.
2. Ejecucion asincrona de validacion/carga.
3. Estado de proceso.

DoD:

1. Usuario puede revisar avance y resultado.

### S5-T03: Reporte de errores

1. Generar CSV de observaciones.
2. Clasificar severidad (`ERROR`, `WARNING`).

DoD:

1. Archivo de salida descargable.

### S5-T04: Exportacion versionada

1. Plantilla por version.
2. Endpoint export por periodo/seccion.

DoD:

1. Version queda registrada por tenant.

### S5-T05: UI web de import/export

1. Pantalla subida con preview de validacion.
2. Historial de procesos.

DoD:

1. Flujo usable sin soporte tecnico continuo.

### S5-T06: QA tecnico

1. Archivos validos, invalidos y mixtos.
2. Casos de encoding y columnas faltantes.

DoD:

1. Cobertura de casos criticos en verde.

### S5-T07: Manual operativo

1. Guia corta paso a paso.
2. Mapa de errores comunes.

DoD:

1. Documento validado por tu amigo.

### S5-T08: UAT

1. Simulacion de flujo de secretaria.

DoD:

1. Acta UAT con observaciones cerradas.

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

1. T07 y T08.
2. Demo sprint.

## 5. Criterios de Exito

1. Import/export funcional con errores entendibles.
2. Secretaria puede corregir sin depender de desarrollo.
3. Historial y versionado activos.

## 6. Riesgos y Contencion

1. Variaciones de archivos de entrada.  
Accion: tolerancia controlada + validaciones estrictas por campo.

2. Cargas grandes lentas.  
Accion: procesamiento asincrono y limite por archivo.

## 7. Salida a Sprint 6

1. Flujo academico integral completo.
2. Listo para hardening antes de cobranza.
