package com.gestion.equipos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AreaFilterDTO {
    
    private String nombre;
    private Boolean activo;
    private Integer responsableId;
    
    private int page = 0;
    private int size = 10;
    private String sort = "fechaCommit";
    private String direction = "desc";
}