package com.gestion.equipos.service;

import com.gestion.equipos.dto.UbicacionCreateDTO;
import com.gestion.equipos.dto.UbicacionDTO;
import com.gestion.equipos.dto.UbicacionFilterDTO;
import com.gestion.equipos.dto.UbicacionUpdateDTO;
import com.gestion.equipos.entity.QUbicacion;
import com.gestion.equipos.entity.Ubicacion;
import com.gestion.equipos.repository.UbicacionRepository;
import com.querydsl.core.BooleanBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UbicacionServiceImpl implements UbicacionService {
    
    @Autowired
    private UbicacionRepository ubicacionRepository;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @Override
    @Transactional(readOnly = true)
    public Page<Ubicacion> findAllWithFilters(UbicacionFilterDTO filters, Pageable pageable) {
        BooleanBuilder predicate = new BooleanBuilder();
        QUbicacion ubicacion = QUbicacion.ubicacion;
        
        predicate.and(ubicacion.activo.isTrue());
        
        if (StringUtils.hasText(filters.getNombre())) {
            predicate.and(ubicacion.nombre.containsIgnoreCase(filters.getNombre().trim()));
        }
        
        if (StringUtils.hasText(filters.getDescripcion())) {
            predicate.and(ubicacion.descripcion.containsIgnoreCase(filters.getDescripcion().trim()));
        }
        
        return ubicacionRepository.findAll(predicate, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UbicacionDTO> findAllUbicacionDTOWithFilters(UbicacionFilterDTO filters, Pageable pageable) {
        Page<Ubicacion> ubicacionPage = findAllWithFilters(filters, pageable);
        
        List<UbicacionDTO> ubicacionDTOs = ubicacionPage.getContent().stream()
                .map(this::convertToDTO)
                .toList();
        
        return new org.springframework.data.domain.PageImpl<>(ubicacionDTOs, pageable, ubicacionPage.getTotalElements());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Ubicacion> findAllActive() {
        return ubicacionRepository.findByActivoTrueOrderByNombreAsc();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Ubicacion> findById(Integer id) {
        return ubicacionRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<UbicacionDTO> findUbicacionDTOById(Integer id) {
        return ubicacionRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional
    public UbicacionDTO create(UbicacionCreateDTO createDTO) {
        String nombreTrimmed = createDTO.getNombre().trim();
        
        Optional<Ubicacion> ubicacionInactiva = ubicacionRepository.findByNombreIgnoreCaseAndActivoFalse(nombreTrimmed);
        if (ubicacionInactiva.isPresent()) {
            Ubicacion ubicacion = ubicacionInactiva.get();
            ubicacion.setActivo(true);
            ubicacion.setDescripcion(createDTO.getDescripcion() != null ? createDTO.getDescripcion().trim() : null);
            ubicacion.setFechaCommit(LocalDateTime.now());
            return convertToDTO(ubicacionRepository.save(ubicacion));
        }
        
        Optional<Ubicacion> ubicacionActiva = ubicacionRepository.findByNombreIgnoreCaseAndActivoTrue(nombreTrimmed);
        if (ubicacionActiva.isPresent()) {
            throw new IllegalArgumentException("Ya existe una ubicación activa con ese nombre");
        }
        
        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setNombre(nombreTrimmed);
        ubicacion.setDescripcion(createDTO.getDescripcion() != null ? createDTO.getDescripcion().trim() : null);
        ubicacion.setActivo(true);
        ubicacion.setCreadoPor(authService.getCurrentUser());
        ubicacion.setFechaCommit(LocalDateTime.now());
        
        return convertToDTO(ubicacionRepository.save(ubicacion));
    }
    
    @Override
    @Transactional
    public UbicacionDTO update(Integer id, UbicacionUpdateDTO updateDTO) {
        Ubicacion ubicacion = ubicacionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Ubicación no encontrada"));
        
        String nombreTrimmed = updateDTO.getNombre().trim();
        
        Optional<Ubicacion> ubicacionConMismoNombre = ubicacionRepository.findByNombreIgnoreCaseAndIdNot(nombreTrimmed, id);
        if (ubicacionConMismoNombre.isPresent()) {
            throw new IllegalArgumentException("Ya existe otra ubicación con ese nombre");
        }
        
        ubicacion.setNombre(nombreTrimmed);
        ubicacion.setDescripcion(updateDTO.getDescripcion() != null ? updateDTO.getDescripcion().trim() : null);
        ubicacion.setFechaCommit(LocalDateTime.now());
        
        return convertToDTO(ubicacionRepository.save(ubicacion));
    }
    
    @Override
    @Transactional
    public void delete(Integer id) {
        Ubicacion ubicacion = ubicacionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Ubicación no encontrada"));
        
        ubicacion.setActivo(false);
        ubicacion.setFechaCommit(LocalDateTime.now());
        ubicacionRepository.save(ubicacion);
    }
    
    @Override
    public UbicacionDTO convertToDTO(Ubicacion ubicacion) {
        UbicacionDTO dto = new UbicacionDTO();
        dto.setId(ubicacion.getId());
        dto.setNombre(ubicacion.getNombre());
        dto.setDescripcion(ubicacion.getDescripcion());
        dto.setActivo(ubicacion.getActivo());
        dto.setFechaCommit(ubicacion.getFechaCommit());
        
        if (ubicacion.getCreadoPor() != null) {
            dto.setCreadoPor(ubicacion.getCreadoPor().getPrimerNombre() + " " + ubicacion.getCreadoPor().getPrimerApellido());
        }
        
        return dto;
    }
}