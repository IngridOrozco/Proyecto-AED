-- SGAI · Consultas de prueba para MySQL 8.
-- Instalación limpia: ejecutar después de crear la base con esquema.sql.
-- Modelo Nota: la tabla académica se llama notas y su columna de valor es nota.
-- Las notas individuales son INT (0..100); los promedios conservan decimales.
-- Se pueden ejecutar juntas o seleccionar una consulta en MySQL Workbench.
-- Los ejemplos 1 a 19 son de lectura. El ejemplo 20 ejecuta un UPDATE de prueba
-- y lo revierte con ROLLBACK: no deja cambios permanentes.
-- Ejecutar el ejemplo 20 COMPLETO en una conexión nueva y dedicada, sin trabajo
-- pendiente; START TRANSACTION confirma cualquier transacción anterior abierta.
-- Si se interrumpe antes del ROLLBACK, ejecutar ROLLBACK en esa misma conexión.
-- Con una base vacía, los listados no devolverán filas y los conteos serán cero.
-- La carga de datos_demostracion.sql es opcional y solo se recomienda en pruebas.
-- Usar una cuenta autorizada (SELECT y UPDATE para el ejemplo 20): estos scripts
-- no aplican los filtros por rol de la aplicación. No usarlos sobre datos reales.

USE `sgai-soldano-db`;

-- 1. Listar estudiantes por grado y apellido.
SELECT codigo, nombres, apellidos, correo, grado
FROM estudiantes
ORDER BY grado, apellidos, nombres, codigo;

-- 2. Consultar los docentes y su especialidad registrada.
SELECT codigo, nombres, apellidos, correo, especialidad
FROM docentes
ORDER BY apellidos, nombres, codigo;

-- 3. Consultar el catálogo de cursos por grado.
SELECT codigo, nombre, grado
FROM cursos
ORDER BY grado, nombre, codigo;

-- 4. Contar los estudiantes registrados en cada grado con registros.
SELECT grado, COUNT(*) AS cantidad_estudiantes
FROM estudiantes
GROUP BY grado
ORDER BY grado;

-- 5. SELECT con INNER JOIN: mostrar cursos matriculados de cada estudiante.
SELECT e.codigo AS codigo_estudiante,
       CONCAT(e.nombres, ' ', e.apellidos) AS estudiante,
       e.grado, c.codigo AS codigo_curso, c.nombre AS curso
FROM matriculas m
INNER JOIN estudiantes e ON e.id = m.estudiante_id
INNER JOIN cursos c ON c.id = m.curso_id
ORDER BY e.grado, e.codigo, c.codigo;

-- 6. Contar cursos matriculados, incluyendo estudiantes sin matrícula.
SELECT e.codigo, e.nombres, e.apellidos, COUNT(m.curso_id) AS cursos_matriculados
FROM estudiantes e
LEFT JOIN matriculas m ON m.estudiante_id = e.id
GROUP BY e.id, e.codigo, e.nombres, e.apellidos
ORDER BY e.codigo;

-- 7. Encontrar estudiantes sin cursos matriculados.
SELECT e.codigo, e.nombres, e.apellidos, e.grado
FROM estudiantes e
WHERE NOT EXISTS (
    SELECT 1 FROM matriculas m WHERE m.estudiante_id = e.id
)
ORDER BY e.grado, e.codigo;

-- 8. Ver qué docentes están asignados a cada curso.
SELECT c.codigo AS codigo_curso, c.nombre AS curso, c.grado,
       d.codigo AS codigo_docente,
       CONCAT(d.nombres, ' ', d.apellidos) AS docente, d.especialidad
FROM asignacion_docente_curso a
INNER JOIN cursos c ON c.id = a.curso_id
INNER JOIN docentes d ON d.id = a.docente_id
ORDER BY c.grado, c.codigo, d.codigo;

-- 9. Encontrar cursos que todavía no tienen un docente asignado.
SELECT c.codigo, c.nombre, c.grado
FROM cursos c
WHERE NOT EXISTS (
    SELECT 1 FROM asignacion_docente_curso a WHERE a.curso_id = c.id
)
ORDER BY c.grado, c.codigo;

-- 10. Consultar notas con estudiante, curso, docente y período parcial.
SELECT e.codigo AS codigo_estudiante,
       CONCAT(e.nombres, ' ', e.apellidos) AS estudiante,
       c.codigo AS codigo_curso, c.nombre AS curso,
       CONCAT(d.nombres, ' ', d.apellidos) AS docente,
       n.periodo_parcial, n.nota
FROM notas n
INNER JOIN estudiantes e ON e.id = n.estudiante_id
INNER JOIN cursos c ON c.id = n.curso_id
INNER JOIN docentes d ON d.id = n.docente_id
ORDER BY e.codigo, c.codigo, n.periodo_parcial;

-- 11. Localizar notas menores a 60, sin confundirlas con el resultado final del curso.
SELECT e.codigo AS codigo_estudiante, c.codigo AS codigo_curso,
       c.nombre AS curso, n.periodo_parcial, n.nota
FROM notas n
INNER JOIN estudiantes e ON e.id = n.estudiante_id
INNER JOIN cursos c ON c.id = n.curso_id
WHERE n.nota < 60
ORDER BY n.nota, e.codigo, c.codigo, n.periodo_parcial;

-- 12. Calcular el resultado de cada curso matriculado.
-- El promedio parcial usa solo notas existentes. El resultado final requiere los
-- cuatro períodos parciales; una nota ausente no se reemplaza por cero.
-- La aprobación usa el promedio redondeado a dos decimales, como en el reporte Java.
SELECT e.codigo AS codigo_estudiante, c.codigo AS codigo_curso,
       c.nombre AS curso, COUNT(n.id) AS periodos_registrados,
       ROUND(AVG(n.nota), 2) AS promedio_registrado,
       CASE WHEN COUNT(n.id) = 4 THEN ROUND(AVG(n.nota), 2)
            ELSE NULL END AS promedio_final,
       CASE WHEN COUNT(n.id) < 4 THEN 'PENDIENTE'
            WHEN ROUND(AVG(n.nota), 2) >= 60 THEN 'APROBADO'
            ELSE 'REPROBADO' END AS resultado
FROM matriculas m
INNER JOIN estudiantes e ON e.id = m.estudiante_id
INNER JOIN cursos c ON c.id = m.curso_id
LEFT JOIN notas n ON n.estudiante_id = m.estudiante_id
                         AND n.curso_id = m.curso_id
GROUP BY e.id, e.codigo, c.id, c.codigo, c.nombre
ORDER BY e.codigo, c.codigo;

-- 13. Detectar matrículas incompatibles con el grado del estudiante.
-- Resultado esperado: ninguna fila. La aplicación impide estas matrículas.
SELECT e.codigo AS codigo_estudiante, e.grado AS grado_estudiante,
       c.codigo AS codigo_curso, c.grado AS grado_curso
FROM matriculas m
INNER JOIN estudiantes e ON e.id = m.estudiante_id
INNER JOIN cursos c ON c.id = m.curso_id
WHERE e.grado <> c.grado
ORDER BY e.codigo, c.codigo;

-- 14. Consultar cuentas y personas asociadas sin mostrar contraseñas ni hashes.
-- Las cuentas administrativas deben tener ambos códigos asociados en NULL.
SELECT u.nombre AS usuario, u.rol,
       e.codigo AS codigo_estudiante, d.codigo AS codigo_docente
FROM usuarios u
LEFT JOIN estudiantes e ON e.id = u.estudiante_id
LEFT JOIN docentes d ON d.id = u.docente_id
ORDER BY u.rol, u.nombre;

-- 15. Identificar estudiantes cuya matrícula no se puede eliminar por tener notas.
-- La protección debe aplicarse a toda la matrícula, no solo al curso con notas.
SELECT e.codigo, e.nombres, e.apellidos, COUNT(n.id) AS notas_registradas
FROM estudiantes e
INNER JOIN notas n ON n.estudiante_id = e.id
GROUP BY e.id, e.codigo, e.nombres, e.apellidos
ORDER BY e.codigo;


-- 16. WHERE con condiciones múltiples unidas por AND: verificar el tipo de nota.
-- Resultado esperado: nota, int, NO. Debe coincidir con el esquema.sql de esta versión.
SELECT COLUMN_NAME AS columna, DATA_TYPE AS tipo, IS_NULLABLE AS admite_nulos
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'notas'
  AND COLUMN_NAME = 'nota';

-- 17. Mostrar los cuatro períodos parciales por cada curso matriculado.
-- Como en el reporte, NULL significa sin nota; no se convierte en cero.
-- MAX selecciona la única nota del período, conservando el valor entero original.
SELECT e.codigo AS codigo_estudiante, c.codigo AS codigo_curso, c.nombre AS curso,
       MAX(CASE WHEN n.periodo_parcial = 1 THEN n.nota END) AS primer_parcial,
       MAX(CASE WHEN n.periodo_parcial = 2 THEN n.nota END) AS segundo_parcial,
       MAX(CASE WHEN n.periodo_parcial = 3 THEN n.nota END) AS tercer_parcial,
       MAX(CASE WHEN n.periodo_parcial = 4 THEN n.nota END) AS cuarto_parcial
FROM matriculas m
INNER JOIN estudiantes e ON e.id = m.estudiante_id
INNER JOIN cursos c ON c.id = m.curso_id
LEFT JOIN notas n ON n.estudiante_id = m.estudiante_id
                         AND n.curso_id = m.curso_id
GROUP BY e.id, e.codigo, c.id, c.codigo, c.nombre
ORDER BY e.codigo, c.codigo;

-- 18. WHERE con condiciones múltiples unidas por OR: comprobar rangos.
-- Resultado esperado: ninguna fila. Las restricciones del esquema impiden valores fuera de rango.
SELECT id, estudiante_id, curso_id, periodo_parcial, nota
FROM notas
WHERE nota NOT BETWEEN 0 AND 100
   OR periodo_parcial NOT BETWEEN 1 AND 4
ORDER BY id;

-- 19. Consultar matrículas que se pueden retirar sin afectar notas registradas.
-- Se excluye al estudiante completo si tiene al menos una nota en cualquier curso.
-- Solo es una consulta; no ejecuta la eliminación ni sustituye la autorización.
SELECT e.codigo, e.nombres, e.apellidos, COUNT(m.curso_id) AS cursos_a_retirar
FROM estudiantes e
INNER JOIN matriculas m ON m.estudiante_id = e.id
WHERE NOT EXISTS (
    SELECT 1 FROM notas n WHERE n.estudiante_id = e.id
)
GROUP BY e.id, e.codigo, e.nombres, e.apellidos
ORDER BY e.codigo;


-- 20. UPDATE de prueba: cambiar temporalmente el correo de un estudiante ficticio.
-- Solo selecciona un registro del lote de datos_demostracion.sql y nunca uno real.
-- Si el lote no está cargado, la variable queda en NULL y el UPDATE afecta cero filas.
-- Ejecutar todo el bloque, incluido ROLLBACK y la consulta de comprobación final.
START TRANSACTION;

SET @estudiante_prueba = (
    SELECT id FROM estudiantes
    WHERE correo LIKE '%@sgai-demo-v1.invalid'
    ORDER BY id
    LIMIT 1
);

SELECT codigo, correo AS correo_original
FROM estudiantes
WHERE id = @estudiante_prueba;

UPDATE estudiantes
SET correo = 'consulta-prueba@sgai-demo-v1.invalid'
WHERE id = @estudiante_prueba
  AND correo LIKE '%@sgai-demo-v1.invalid'
LIMIT 1;

SELECT codigo, correo AS correo_temporal
FROM estudiantes
WHERE id = @estudiante_prueba;

-- Deshacer la modificación; no sustituir por COMMIT para esta prueba.
ROLLBACK;

SELECT codigo, correo AS correo_restaurado
FROM estudiantes
WHERE id = @estudiante_prueba;
