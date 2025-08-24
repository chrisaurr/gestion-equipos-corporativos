package com.gestion.equipos.dto;

import com.gestion.equipos.entity.enums.EstadoUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioFilterDTO {
    
    private String codigo;
    private String usuario;
    private String primerNombre;
    private String primerApellido;
    private EstadoUsuario estado;
    private LocalDate fechaIngresoDesde;
    private LocalDate fechaIngresoHasta;
    private Boolean isAdmin;
    private Boolean esEmpleado;
    private Integer areaId;
    
    private int page = 0;
    private int size = 10;
    private String sort = "fechaCommit";
    private String direction = "desc";
}