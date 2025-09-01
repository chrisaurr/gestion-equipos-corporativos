package com.gestion.equipos.service;

import com.gestion.equipos.dto.CausaCreateDTO;
import com.gestion.equipos.dto.CausaDTO;
import com.gestion.equipos.dto.CausaUpdateDTO;
import com.gestion.equipos.entity.Causa;
import com.gestion.equipos.entity.QCausa;
import com.gestion.equipos.repository.CausaRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CausaServiceImpl implements CausaService {
    
    private final CausaRepository causaRepository;
    private final AuthService authService;
    
    @Override
    @Transactional(readOnly = true)
    public Page<CausaDTO> findAll(String nombre, String descripcion, Boolean activo, Pageable pageable) {
        QCausa qCausa = QCausa.causa;
        BooleanBuilder builder = new BooleanBuilder();
        
        // SIEMPRE filtrar solo activos para la tabla principal
        builder.and(qCausa.activo.isTrue());
        
        if (nombre != null && !nombre.trim().isEmpty()) {
            builder.and(qCausa.nombre.containsIgnoreCase(nombre.trim()));
        }
        
        if (descripcion != null && !descripcion.trim().isEmpty()) {
            builder.and(qCausa.descripcion.containsIgnoreCase(descripcion.trim()));
        }
        
        return causaRepository.findAll(builder, pageable)
                .map(CausaDTO::fromEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CausaDTO> findAllActive() {
        return causaRepository.findByActivoTrueOrderByNombreAsc()
                .stream()
                .map(CausaDTO::fromEntity)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<CausaDTO> findById(Integer id) {
        return causaRepository.findById(id)
                .map(CausaDTO::fromEntity);
    }
    
    @Override
    @Transactional
    public CausaDTO create(CausaCreateDTO causaCreateDTO) {
        String nombreTrimmed = causaCreateDTO.getNombre().trim();
        
        // Primero verificar si existe una inactiva con el mismo nombre
        Optional<Causa> causaInactiva = causaRepository.findByNombreIgnoreCaseAndActivoFalse(nombreTrimmed);
        if (causaInactiva.isPresent()) {
            // Activar la existente
            Causa causa = causaInactiva.get();
            causa.setActivo(true);
            causa.setDescripcion(causaCreateDTO.getDescripcion());
            causa.setPrioridadReporte(causaCreateDTO.getPrioridadReporte());
            causa.setFechaCommit(java.time.LocalDateTime.now());
            Causa savedCausa = causaRepository.save(causa);
            return CausaDTO.fromEntity(savedCausa);
        }
        
        // Verificar si existe una activa
        Optional<Causa> causaActiva = causaRepository.findByNombreIgnoreCaseAndActivoTrue(nombreTrimmed);
        if (causaActiva.isPresent()) {
            throw new IllegalArgumentException("Ya existe una causa activa con ese nombre");
        }
        
        // Crear nueva causa
        Causa causa = new Causa();
        causa.setNombre(nombreTrimmed);
        causa.setDescripcion(causaCreateDTO.getDescripcion());
        causa.setPrioridadReporte(causaCreateDTO.getPrioridadReporte());
        causa.setActivo(true);
        causa.setCreadoPor(authService.getCurrentUser());
        
        Causa savedCausa = causaRepository.save(causa);
        return CausaDTO.fromEntity(savedCausa);
    }
    
    @Override
    @Transactional
    public CausaDTO update(Integer id, CausaUpdateDTO causaUpdateDTO) {
        Causa causa = causaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Causa no encontrada"));
        
        String nombreTrimmed = causaUpdateDTO.getNombre().trim();
        
        // Verificar que no exista otra causa con el mismo nombre
        Optional<Causa> causaConMismoNombre = causaRepository.findByNombreIgnoreCaseAndIdNot(nombreTrimmed, id);
        if (causaConMismoNombre.isPresent()) {
            throw new IllegalArgumentException("Ya existe otra causa con ese nombre");
        }
        
        causa.setNombre(nombreTrimmed);
        causa.setDescripcion(causaUpdateDTO.getDescripcion());
        causa.setPrioridadReporte(causaUpdateDTO.getPrioridadReporte());
        causa.setFechaCommit(java.time.LocalDateTime.now());
        
        Causa savedCausa = causaRepository.save(causa);
        return CausaDTO.fromEntity(savedCausa);
    }
    
    @Override
    @Transactional
    public void delete(Integer id) {
        Causa causa = causaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Causa no encontrada"));
        
        causa.setActivo(false);
        causa.setFechaCommit(java.time.LocalDateTime.now());
        causaRepository.save(causa);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByNombre(String nombre) {
        return causaRepository.findByNombreAndActivoTrue(nombre).isPresent();
    }
}