-- Agregar campo activo a la tabla reporte para soft delete
ALTER TABLE reporte ADD COLUMN activo boolean NOT NULL DEFAULT true;