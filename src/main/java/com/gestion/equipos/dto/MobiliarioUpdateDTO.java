package com.gestion.equipos.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MobiliarioUpdateDTO {
    
    @NotBlank(message = "El material es obligatorio")
    @Size(max = 50, message = "El material no puede exceder 50 caracteres")
    private String material;
    
    @NotNull(message = "La altura es obligatoria")
    @DecimalMin(value = "0.0", message = "La altura no puede ser negativa")
    private BigDecimal altura;
    
    @NotNull(message = "El ancho es obligatorio")
    @DecimalMin(value = "0.0", message = "El ancho no puede ser negativo")
    private BigDecimal ancho;
    
    @NotNull(message = "La profundidad es obligatoria")
    @DecimalMin(value = "0.0", message = "La profundidad no puede ser negativa")
    private BigDecimal profundidad;
    
    private Integer cantidadPiezas;
    
    public void validate() {
        if (material == null || material.trim().isEmpty()) {
            throw new IllegalArgumentException("El material es obligatorio para mobiliario");
        }
        
        if (altura == null || altura.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La altura es obligatoria y debe ser mayor a 0");
        }
        
        if (ancho == null || ancho.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El ancho es obligatorio y debe ser mayor a 0");
        }
        
        if (profundidad == null || profundidad.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La profundidad es obligatoria y debe ser mayor a 0");
        }
    }
}