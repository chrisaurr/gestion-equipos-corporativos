package com.gestion.equipos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionFilterDTO {
    
    private String nombre;
    private String descripcion;
    private int page = 0;
    private int size = 10;
    private String sort = "nombre";
    private String direction = "asc";
}