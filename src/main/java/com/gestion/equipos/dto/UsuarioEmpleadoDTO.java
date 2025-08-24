package com.gestion.equipos.dto;

import com.gestion.equipos.entity.enums.EstadoUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioEmpleadoDTO {
    
    private Integer usuarioId;
    private String codigo;
    private String usuario;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private String nombreCompleto;
    private EstadoUsuario estado;
    private LocalDate fechaIngreso;
    private LocalDate fechaSalida;
    private Boolean isAdmin;
    private LocalDateTime fechaCommit;
    
    private Integer empleadoId;
    private Integer areaId;
    private String areaNombre;
    private String cargo;
    private String observaciones;
    private Boolean esEmpleado = false;
    
    public String getNombreCompleto() {
        StringBuilder nombre = new StringBuilder();
        if (primerNombre != null) nombre.append(primerNombre);
        if (segundoNombre != null) nombre.append(" ").append(segundoNombre);
        if (primerApellido != null) nombre.append(" ").append(primerApellido);
        if (segundoApellido != null) nombre.append(" ").append(segundoApellido);
        return nombre.toString().trim();
    }
    
    public Boolean getEsEmpleado() {
        return empleadoId != null;
    }
}