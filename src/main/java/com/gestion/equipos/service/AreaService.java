package com.gestion.equipos.service;

import com.gestion.equipos.dto.AreaDTO;
import com.gestion.equipos.entity.Area;
import com.gestion.equipos.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface AreaService {
    
    List<Area> findAll();
    
    List<Area> findAllActive();
    
    Optional<Area> findById(Integer id);
    
    Page<Area> findAllWithFilters(String nombre, Boolean activo, Integer responsableId, Pageable pageable);
    
    Page<AreaDTO> findAllAreaDTOWithFilters(String nombre, Boolean activo, Integer responsableId, Pageable pageable);
    
    Optional<AreaDTO> findAreaDTOById(Integer id);
    
    Area save(Area area);
    
    Area update(Integer id, Area area);
    
    void deleteById(Integer id);
    
    boolean existsByNombre(String nombre);
    
    boolean existsByNombreAndIdNot(String nombre, Integer id);
    
    void asignarResponsable(Integer areaId, Integer usuarioId);
    
    void removerResponsable(Integer areaId);
    
    boolean hasEmpleadosActivos(Integer areaId);
}