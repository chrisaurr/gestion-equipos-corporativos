package com.gestion.equipos.dto;

import com.gestion.equipos.entity.enums.PrioridadReporte;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CausaDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Boolean activo;
    private PrioridadReporte prioridadReporte;
    private String creadoPor;
    private LocalDateTime fechaCommit;
    
    public static CausaDTO fromEntity(com.gestion.equipos.entity.Causa causa) {
        CausaDTO dto = new CausaDTO();
        dto.setId(causa.getId());
        dto.setNombre(causa.getNombre());
        dto.setDescripcion(causa.getDescripcion());
        dto.setActivo(causa.getActivo());
        dto.setPrioridadReporte(causa.getPrioridadReporte());
        dto.setFechaCommit(causa.getFechaCommit());
        
        if (causa.getCreadoPor() != null) {
            dto.setCreadoPor(causa.getCreadoPor().getPrimerNombre() + " " + 
                           causa.getCreadoPor().getPrimerApellido());
        }
        
        return dto;
    }
}