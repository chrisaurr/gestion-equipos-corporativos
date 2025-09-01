package com.gestion.equipos.dto;

import lombok.Data;

@Data
public class ReporteFilterDTO {
    
    private String observacion;
    private String estado;
    private Integer causaId;
    private Integer equipoId;
    private Integer usuarioId;
    private String fechaDesde;
    private String fechaHasta;
    private Boolean activo;
    
    private int page = 0;
    private int size = 10;
    private String sortBy = "fechaCommit";
    private String sortDir = "desc";
}