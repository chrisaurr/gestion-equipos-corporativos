package com.gestion.equipos.service;

import com.gestion.equipos.entity.Area;
import com.gestion.equipos.entity.Empleado;
import com.gestion.equipos.entity.HistorialEmpleado;
import com.gestion.equipos.entity.enums.TipoCambioEmpleado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HistorialEmpleadoService {
    
    HistorialEmpleado save(HistorialEmpleado historial);
    
    Optional<HistorialEmpleado> findById(Integer id);
    
    List<HistorialEmpleado> findByEmpleado(Empleado empleado);
    
    List<HistorialEmpleado> findByEmpleadoId(Integer empleadoId);
    
    Page<HistorialEmpleado> findByEmpleado(Empleado empleado, Pageable pageable);
    
    Page<HistorialEmpleado> findByEmpleadoId(Integer empleadoId, Pageable pageable);
    
    List<HistorialEmpleado> findByTipoCambio(TipoCambioEmpleado tipoCambio);
    
    List<HistorialEmpleado> findByFechaRange(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    long countByEmpleadoId(Integer empleadoId);
    
    long countByTipoCambio(TipoCambioEmpleado tipoCambio);
    
    // Métodos para registrar cambios específicos con lógica de negocio
    void registrarAscensoJefe(Integer empleadoId, Area area, String nuevoCargo, 
                             String motivo, Integer creadoPorId);
    
    void registrarDescenso(Integer empleadoId, String nuevoCargo, 
                          String motivo, Integer creadoPorId);
}