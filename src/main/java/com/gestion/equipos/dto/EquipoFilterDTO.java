package com.gestion.equipos.dto;

import com.gestion.equipos.entity.enums.EstadoEquipo;
import com.gestion.equipos.entity.enums.TipoEquipo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipoFilterDTO {
    
    // Filtros de búsqueda
    private String identificador;
    private String nombre;
    private String serie;
    private Integer marcaId;
    private TipoEquipo tipoEquipo;
    private EstadoEquipo estado;
    private Integer ubicacionId;
    private Integer usuarioAsignadoId;
    private Boolean asignado; // true = asignados, false = libres, null = todos
    
    // Campos de paginación
    private int page = 0;
    private int size = 10;
    private String sort = "identificador";
    private String direction = "asc";
}