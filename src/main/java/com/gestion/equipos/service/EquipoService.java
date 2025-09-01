package com.gestion.equipos.service;

import com.gestion.equipos.dto.EquipoCreateDTO;
import com.gestion.equipos.dto.EquipoDTO;
import com.gestion.equipos.dto.EquipoFilterDTO;
import com.gestion.equipos.dto.EquipoUpdateDTO;
import com.gestion.equipos.entity.Equipo;
import com.gestion.equipos.entity.enums.EstadoEquipo;
import com.gestion.equipos.entity.enums.TipoEquipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EquipoService {
    
    Page<Equipo> findAllWithFilters(EquipoFilterDTO filters, Pageable pageable);
    
    Page<EquipoDTO> findAllEquipoDTOWithFilters(EquipoFilterDTO filters, Pageable pageable);
    
    List<Equipo> findAllActive();
    
    Optional<Equipo> findById(Integer id);
    
    Optional<EquipoDTO> findEquipoDTOById(Integer id);
    
    EquipoDTO create(EquipoCreateDTO createDTO);
    
    EquipoDTO update(Integer id, EquipoUpdateDTO updateDTO);
    
    void delete(Integer id);
    
    EquipoDTO cambiarEstado(Integer id, EstadoEquipo nuevoEstado, String motivo);
    
    EquipoDTO convertToDTO(Equipo equipo);
    
    List<EquipoDTO> findByTipoEquipo(TipoEquipo tipo);
    
    List<EquipoDTO> findEquiposByUsuario(Integer usuarioId);
    
    List<EquipoDTO> findEquiposLibres();
}