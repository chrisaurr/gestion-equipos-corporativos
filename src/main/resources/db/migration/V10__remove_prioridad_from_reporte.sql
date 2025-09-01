-- V10__remove_prioridad_from_reporte.sql  
-- Quitar campo prioridad de tabla reporte (ahora se hereda de causa)

ALTER TABLE reporte DROP COLUMN prioridad;