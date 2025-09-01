package com.gestion.equipos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReporteCreateDTO {
    
    @NotBlank(message = "La observación es obligatoria")
    private String observacion;
    
    @NotNull(message = "La causa es obligatoria")
    private Integer causaId;
    
    @NotNull(message = "El equipo es obligatorio")
    private Integer equipoId;
}