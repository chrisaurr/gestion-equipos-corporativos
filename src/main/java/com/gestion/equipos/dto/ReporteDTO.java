package com.gestion.equipos.dto;

import com.gestion.equipos.entity.Reporte;
import com.gestion.equipos.entity.enums.EstadoReporte;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReporteDTO {
    
    private Integer id;
    private String observacion;
    private EstadoReporte estado;
    private Boolean activo;
    private LocalDateTime fechaCommit;
    
    // Información de la causa
    private Integer causaId;
    private String causaNombre;
    private String causaDescripcion;
    private String causaPrioridad;
    
    // Información del equipo
    private Integer equipoId;
    private String equipoIdentificador;
    private String equipoNombre;
    private String equipoSerie;
    private String equipoMarca;
    
    // Información del usuario reportante
    private Integer usuarioId;
    private String usuarioNombre;
    private String usuarioApellido;
    
    public static ReporteDTO fromEntity(Reporte reporte) {
        ReporteDTO dto = new ReporteDTO();
        dto.setId(reporte.getId());
        dto.setObservacion(reporte.getObservacion());
        dto.setEstado(reporte.getEstado());
        dto.setActivo(reporte.getActivo());
        dto.setFechaCommit(reporte.getFechaCommit());
        
        // Mapear información de la causa
        if (reporte.getIdCausa() != null) {
            dto.setCausaId(reporte.getIdCausa().getId());
            dto.setCausaNombre(reporte.getIdCausa().getNombre());
            dto.setCausaDescripcion(reporte.getIdCausa().getDescripcion());
            dto.setCausaPrioridad(reporte.getIdCausa().getPrioridadReporte().name());
        }
        
        // Mapear información del equipo
        if (reporte.getIdEquipo() != null) {
            dto.setEquipoId(reporte.getIdEquipo().getId());
            dto.setEquipoIdentificador(reporte.getIdEquipo().getIdentificador());
            dto.setEquipoNombre(reporte.getIdEquipo().getNombre());
            dto.setEquipoSerie(reporte.getIdEquipo().getSerie());
            if (reporte.getIdEquipo().getIdMarca() != null) {
                dto.setEquipoMarca(reporte.getIdEquipo().getIdMarca().getNombre());
            }
        }
        
        // Mapear información del usuario
        if (reporte.getIdUsuario() != null) {
            dto.setUsuarioId(reporte.getIdUsuario().getId());
            dto.setUsuarioNombre(reporte.getIdUsuario().getPrimerNombre());
            dto.setUsuarioApellido(reporte.getIdUsuario().getPrimerApellido());
        }
        
        return dto;
    }
}