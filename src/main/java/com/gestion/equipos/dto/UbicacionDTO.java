package com.gestion.equipos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionDTO {
    
    private Integer id;
    private String nombre;
    private String descripcion;
    private Boolean activo;
    private String creadoPor;
    private LocalDateTime fechaCommit;
}