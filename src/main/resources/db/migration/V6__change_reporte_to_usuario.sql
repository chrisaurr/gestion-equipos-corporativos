-- Cambiar reporte de id_empleado a id_usuario para permitir reportes de cualquier usuario
ALTER TABLE reporte 
DROP CONSTRAINT IF EXISTS fk_reporte_empleado;

ALTER TABLE reporte 
RENAME COLUMN id_empleado TO id_usuario;

ALTER TABLE reporte 
ADD CONSTRAINT fk_reporte_usuario 
FOREIGN KEY (id_usuario) REFERENCES usuario(id);