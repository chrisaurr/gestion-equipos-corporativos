package com.gestion.equipos.service;

import com.gestion.equipos.dto.EquipoDTO;
import com.gestion.equipos.entity.enums.MotivoCambio;

public interface EquipoAsignacionService {
    
    EquipoDTO asignarEquipo(Integer equipoId, Integer usuarioId, String motivo);
    
    EquipoDTO asignarEquipoConMotivo(Integer equipoId, Integer usuarioId, MotivoCambio motivoCambio, String motivo);
    
    EquipoDTO liberarEquipo(Integer equipoId, MotivoCambio motivoCambio, String motivo);
}