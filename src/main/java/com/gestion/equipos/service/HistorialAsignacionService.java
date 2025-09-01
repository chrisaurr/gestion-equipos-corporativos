package com.gestion.equipos.service;

import com.gestion.equipos.entity.HistorialAsignacion;
import com.gestion.equipos.entity.enums.EstadoDevolucion;

import java.util.List;
import java.util.Optional;

public interface HistorialAsignacionService {
    
    List<com.gestion.equipos.dto.HistorialAsignacionDTO> findHistorialByEquipo(Integer equipoId);
    
    boolean existsByIdEquipo_Id(Integer equipoId);
    
    Optional<HistorialAsignacion> findPendingReturnByEquipo(Integer equipoId);
    
    void confirmarDevolucion(Integer historialId);
}