package com.gestion.equipos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HerramientaCreateDTO {
    
    @NotBlank(message = "El material es obligatorio")
    @Size(max = 50, message = "El material no puede exceder 50 caracteres")
    private String material;
    
    public void validate() {
        if (material == null || material.trim().isEmpty()) {
            throw new IllegalArgumentException("El material es obligatorio para herramientas");
        }
    }
}