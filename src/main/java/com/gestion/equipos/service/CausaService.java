package com.gestion.equipos.service;

import com.gestion.equipos.dto.CausaCreateDTO;
import com.gestion.equipos.dto.CausaDTO;
import com.gestion.equipos.dto.CausaUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CausaService {
    
    Page<CausaDTO> findAll(String nombre, String descripcion, Boolean activo, Pageable pageable);
    
    List<CausaDTO> findAllActive();
    
    Optional<CausaDTO> findById(Integer id);
    
    CausaDTO create(CausaCreateDTO causaCreateDTO);
    
    CausaDTO update(Integer id, CausaUpdateDTO causaUpdateDTO);
    
    void delete(Integer id);
    
    boolean existsByNombre(String nombre);
}