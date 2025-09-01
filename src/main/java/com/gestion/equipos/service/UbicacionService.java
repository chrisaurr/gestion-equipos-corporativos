package com.gestion.equipos.service;

import com.gestion.equipos.dto.UbicacionCreateDTO;
import com.gestion.equipos.dto.UbicacionDTO;
import com.gestion.equipos.dto.UbicacionFilterDTO;
import com.gestion.equipos.dto.UbicacionUpdateDTO;
import com.gestion.equipos.entity.Ubicacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UbicacionService {
    
    Page<Ubicacion> findAllWithFilters(UbicacionFilterDTO filters, Pageable pageable);
    
    Page<UbicacionDTO> findAllUbicacionDTOWithFilters(UbicacionFilterDTO filters, Pageable pageable);
    
    List<Ubicacion> findAllActive();
    
    Optional<Ubicacion> findById(Integer id);
    
    Optional<UbicacionDTO> findUbicacionDTOById(Integer id);
    
    UbicacionDTO create(UbicacionCreateDTO createDTO);
    
    UbicacionDTO update(Integer id, UbicacionUpdateDTO updateDTO);
    
    void delete(Integer id);
    
    UbicacionDTO convertToDTO(Ubicacion ubicacion);
}