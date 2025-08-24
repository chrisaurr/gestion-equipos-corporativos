package com.gestion.equipos.dto;

import com.gestion.equipos.entity.enums.TipoCambioEmpleado;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HistorialEmpleadoDTO {
    private Integer id;
    private TipoCambioEmpleado tipoCambio;
    private String cargoAnterior;
    private String cargoNuevo;
    private String motivo;
    private LocalDateTime fechaCambio;
    
    // Información de área anterior
    private Integer areaAnteriorId;
    private String areaAnteriorNombre;
    
    // Información de área nueva
    private Integer areaNuevaId;
    private String areaNuevaNombre;
    
    // Información de quien creó el registro
    private String creadoPor;
}