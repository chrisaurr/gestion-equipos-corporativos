package com.gestion.equipos.dto;

import com.gestion.equipos.entity.enums.PrioridadReporte;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CausaCreateDTO {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    private String nombre;
    
    @Size(max = 300, message = "La descripción no puede exceder 300 caracteres")
    private String descripcion;
    
    @NotNull(message = "La prioridad es obligatoria")
    private PrioridadReporte prioridadReporte;
}