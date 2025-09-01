package com.gestion.equipos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarcaFilterDTO {
    
    private String nombre;
    
    private int page = 0;
    private int size = 10;
    private String sort = "nombre";
    private String direction = "asc";
}