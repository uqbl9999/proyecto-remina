# Sprint 3 - Ejecucion Detallada (Semana 3)

Fecha: 2026-02-21  
Objetivo: implementar registro de notas por curso y periodo con validaciones y calculo base.

## 1. Meta del Sprint

Habilitar flujo docente de notas:

1. Configurar periodos y escalas.
2. Registrar notas individuales y masivas.
3. Calcular promedio por periodo.
4. Exponer avance por docente.

## 2. Dependencias de entrada

1. Asistencia estable del Sprint 2.
2. Modelo academico base activo.

## 3. Tickets de Sprint

### S3-T01: Modelo de notas

1. Tablas `grading_periods`, `assessments`, `grade_records`, `grade_scales`.
2. Relacion por tenant y curso.
3. Soporte a escala numerica y cualitativa.

DoD:

1. Migraciones validadas.
2. Datos seed minimos para pruebas.

### S3-T02: Configuracion de periodos y escalas

1. API crear/editar periodos.
2. API crear/editar escalas.
3. Validar periodo activo por fecha.

DoD:

1. No se registran notas fuera de periodo activo.
2. Configuracion por tenant.

### S3-T03: API de carga de notas

1. Carga individual.
2. Carga por lote por aula-curso.
3. Edicion controlada por rol.

DoD:

1. Errores por fila se reportan claramente.
2. Operaciones auditadas.

### S3-T04: Motor de calculo v1

1. Reglas de promedio simple ponderado configurable.
2. Redondeo configurable.
3. Marca de nota final por periodo.

DoD:

1. Resultados consistentes con casos de prueba definidos.
2. Pruebas de bordes en verde.

### S3-T05: UI web docente de notas

1. Matriz de alumnos x evaluaciones.
2. Edicion rapida por teclado.
3. Indicadores de guardado.

DoD:

1. Flujo usable para 30 alumnos por aula.
2. Advertencias visibles en errores de formato.

### S3-T06: UI coordinacion academica

1. Vista de avance de carga por docente.
2. Alertas de cursos sin cierre.

DoD:

1. Reporte de pendientes disponible.

### S3-T07: QA

1. Unit tests de calculo.
2. Integration tests de endpoints.
3. E2E web de carga de notas.

DoD:

1. CI verde y cobertura minima acordada.

### S3-T08: UAT funcional

1. Pruebas con rubrica funcional.
2. Ajustes Must/Should/Could.

DoD:

1. Acta de UAT cerrada.

## 4. Plan Diario de Ejecucion

### Dia 1

1. T01 y T02.

### Dia 2

1. T03.
2. Iniciar T04.

### Dia 3

1. Cerrar T04.
2. T05.

### Dia 4

1. T06.
2. Integracion y correcciones.

### Dia 5

1. T07 y T08.
2. Demo sprint.

## 5. Criterios de Exito

1. Docente registra notas sin bloqueos.
2. Promedios correctos segun reglas.
3. Coordinacion visualiza avance de carga.

## 6. Riesgos y Contencion

1. Reglas distintas entre colegios.  
Accion: parametrizacion por tenant.

2. Error humano en carga masiva.  
Accion: validaciones preventivas y confirmacion previa.

## 7. Salida a Sprint 4

1. Base de notas estable.
2. Listo para generacion de libretas.
