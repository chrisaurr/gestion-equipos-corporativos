package com.gestion.equipos.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HistorialAsignacionDTO {
    private Integer id;
    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaDevolucion;
    private String motivoCambio;
    private String estadoDevolucion;
    private String usuarioAnteriorNombre;
    private String usuarioNuevoNombre;
    private String observaciones;
    
    public static HistorialAsignacionDTO fromEntity(com.gestion.equipos.entity.HistorialAsignacion historial) {
        HistorialAsignacionDTO dto = new HistorialAsignacionDTO();
        dto.setId(historial.getId());
        dto.setFechaAsignacion(historial.getFechaAsignacion());
        dto.setFechaDevolucion(historial.getFechaDevolucion());
        dto.setMotivoCambio(historial.getMotivoCambio() != null ? historial.getMotivoCambio().name() : null);
        dto.setEstadoDevolucion(historial.getEstadoDevolucion() != null ? historial.getEstadoDevolucion().name() : null);
        dto.setObservaciones(historial.getObservaciones());
        
        // Usuario anterior
        if (historial.getIdUsuarioAnterior() != null) {
            dto.setUsuarioAnteriorNombre(historial.getIdUsuarioAnterior().getPrimerNombre() + " " + 
                                       historial.getIdUsuarioAnterior().getPrimerApellido());
        }
        
        // Usuario nuevo
        if (historial.getIdUsuarioNuevo() != null) {
            dto.setUsuarioNuevoNombre(historial.getIdUsuarioNuevo().getPrimerNombre() + " " + 
                                    historial.getIdUsuarioNuevo().getPrimerApellido());
        }
        
        return dto;
    }
}