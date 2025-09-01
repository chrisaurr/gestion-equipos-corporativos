package com.gestion.equipos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarcaDTO {
    
    private Integer id;
    private String nombre;
    private Boolean activo;
    private String creadoPor;
    private LocalDateTime fechaCommit;
}