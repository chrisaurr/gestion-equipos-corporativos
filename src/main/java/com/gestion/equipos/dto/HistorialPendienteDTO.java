package com.gestion.equipos.dto;

import lombok.Data;

@Data
public class HistorialPendienteDTO {
    private Integer id;
    private Integer equipoId;
    private String estadoDevolucion;
    
    public static HistorialPendienteDTO fromEntity(com.gestion.equipos.entity.HistorialAsignacion historial) {
        HistorialPendienteDTO dto = new HistorialPendienteDTO();
        dto.setId(historial.getId());
        dto.setEquipoId(historial.getIdEquipo().getId());
        dto.setEstadoDevolucion(historial.getEstadoDevolucion().name());
        return dto;
    }
}