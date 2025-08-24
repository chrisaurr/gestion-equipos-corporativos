package com.gestion.equipos.repository;

import com.gestion.equipos.entity.Empleado;
import com.gestion.equipos.entity.HistorialEmpleado;
import com.gestion.equipos.entity.enums.TipoCambioEmpleado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistorialEmpleadoRepository extends JpaRepository<HistorialEmpleado, Integer> {
    
    List<HistorialEmpleado> findByEmpleadoOrderByFechaCambioDesc(Empleado empleado);
    
    List<HistorialEmpleado> findByEmpleado_IdOrderByFechaCambioDesc(Integer empleadoId);
    
    Page<HistorialEmpleado> findByEmpleadoOrderByFechaCambioDesc(Empleado empleado, Pageable pageable);
    
    Page<HistorialEmpleado> findByEmpleado_IdOrderByFechaCambioDesc(Integer empleadoId, Pageable pageable);
    
    List<HistorialEmpleado> findByTipoCambioOrderByFechaCambioDesc(TipoCambioEmpleado tipoCambio);
    
    List<HistorialEmpleado> findByFechaCambioBetweenOrderByFechaCambioDesc(
            LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    long countByEmpleado_Id(Integer empleadoId);
    
    long countByTipoCambio(TipoCambioEmpleado tipoCambio);
}