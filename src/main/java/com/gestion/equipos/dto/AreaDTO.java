package com.gestion.equipos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AreaDTO {
    
    private Integer id;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede tener más de 50 caracteres")
    private String nombre;
    
    private Integer responsableId;
    private String responsableNombre;
    
    @NotNull(message = "Debe especificar si está activo")
    private Boolean activo = true;
    
    @NotNull(message = "Debe especificar quién lo creó")
    private Integer creadoPorId;
    private String creadoPorNombre;
    
    private LocalDateTime fechaCommit;
}