package com.gestion.equipos.service;

import com.gestion.equipos.dto.MarcaCreateDTO;
import com.gestion.equipos.dto.MarcaDTO;
import com.gestion.equipos.dto.MarcaFilterDTO;
import com.gestion.equipos.dto.MarcaUpdateDTO;
import com.gestion.equipos.entity.Marca;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MarcaService {
    
    Page<Marca> findAllWithFilters(MarcaFilterDTO filters, Pageable pageable);
    
    Page<MarcaDTO> findAllMarcaDTOWithFilters(MarcaFilterDTO filters, Pageable pageable);
    
    List<Marca> findAllActive();
    
    Optional<Marca> findById(Integer id);
    
    Optional<MarcaDTO> findMarcaDTOById(Integer id);
    
    MarcaDTO create(MarcaCreateDTO createDTO);
    
    MarcaDTO update(Integer id, MarcaUpdateDTO updateDTO);
    
    void delete(Integer id);
    
    MarcaDTO convertToDTO(Marca marca);
}