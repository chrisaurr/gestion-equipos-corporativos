package com.gestion.equipos.service;

import com.gestion.equipos.dto.ReporteCreateDTO;
import com.gestion.equipos.dto.ReporteDTO;
import com.gestion.equipos.dto.ReporteFilterDTO;
import com.gestion.equipos.dto.ReporteUpdateDTO;
import com.gestion.equipos.entity.enums.EstadoReporte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReporteService {
    
    Page<ReporteDTO> findAllWithFilters(ReporteFilterDTO filters, Pageable pageable);
    
    List<ReporteDTO> findAllActive();
    
    Optional<ReporteDTO> findById(Integer id);
    
    ReporteDTO create(ReporteCreateDTO reporteCreateDTO);
    
    ReporteDTO update(Integer id, ReporteUpdateDTO reporteUpdateDTO);
    
    void delete(Integer id);
    
    boolean equipoTieneReportesAbiertos(Integer equipoId);
    
    ReporteDTO cambiarEstado(Integer id, EstadoReporte nuevoEstado);
}