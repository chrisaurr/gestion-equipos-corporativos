package com.gestion.equipos.dto;

import com.gestion.equipos.entity.enums.EstadoReporte;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReporteUpdateDTO {
    
    @NotBlank(message = "La observación es obligatoria")
    private String observacion;
    
    @NotNull(message = "La causa es obligatoria")
    private Integer causaId;
    
    @NotNull(message = "El equipo es obligatorio")
    private Integer equipoId;
    
    @NotNull(message = "El estado es obligatorio")
    private EstadoReporte estado;
}