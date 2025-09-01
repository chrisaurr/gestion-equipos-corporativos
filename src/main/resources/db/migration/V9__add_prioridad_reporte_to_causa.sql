-- V9__add_prioridad_reporte_to_causa.sql
-- Agregar campo prioridad_reporte a tabla causa

ALTER TABLE causa 
ADD COLUMN prioridad_reporte prioridad_reporte NOT NULL;