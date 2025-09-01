package com.gestion.equipos.service;

import com.gestion.equipos.dto.ReporteCreateDTO;
import com.gestion.equipos.dto.ReporteDTO;
import com.gestion.equipos.dto.ReporteFilterDTO;
import com.gestion.equipos.dto.ReporteUpdateDTO;
import com.gestion.equipos.entity.*;
import com.gestion.equipos.entity.enums.EstadoReporte;
import com.gestion.equipos.repository.CausaRepository;
import com.gestion.equipos.repository.EquipoRepository;
import com.gestion.equipos.repository.ReporteRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {
    
    private final ReporteRepository reporteRepository;
    private final CausaRepository causaRepository;
    private final EquipoRepository equipoRepository;
    private final AuthService authService;
    
    @Override
    @Transactional(readOnly = true)
    public Page<ReporteDTO> findAllWithFilters(ReporteFilterDTO filters, Pageable pageable) {
        QReporte qReporte = QReporte.reporte;
        BooleanBuilder builder = new BooleanBuilder();
        
        builder.and(qReporte.activo.isTrue());
        
        if (StringUtils.hasText(filters.getObservacion())) {
            builder.and(qReporte.observacion.containsIgnoreCase(filters.getObservacion().trim()));
        }
        
        if (StringUtils.hasText(filters.getEstado())) {
            try {
                EstadoReporte estado = EstadoReporte.valueOf(filters.getEstado());
                builder.and(qReporte.estado.eq(estado));
            } catch (IllegalArgumentException e) {
                // Ignore invalid estado
            }
        }
        
        if (filters.getCausaId() != null) {
            builder.and(qReporte.idCausa.id.eq(filters.getCausaId()));
        }
        
        if (filters.getEquipoId() != null) {
            builder.and(qReporte.idEquipo.id.eq(filters.getEquipoId()));
        }
        
        if (filters.getUsuarioId() != null) {
            builder.and(qReporte.idUsuario.id.eq(filters.getUsuarioId()));
        }
        
        return reporteRepository.findAll(builder, pageable)
                .map(ReporteDTO::fromEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReporteDTO> findAllActive() {
        return reporteRepository.findByActivoTrueOrderByFechaCommitDesc()
                .stream()
                .map(ReporteDTO::fromEntity)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ReporteDTO> findById(Integer id) {
        return reporteRepository.findById(id)
                .map(ReporteDTO::fromEntity);
    }
    
    @Override
    @Transactional
    public ReporteDTO create(ReporteCreateDTO reporteCreateDTO) {
        Optional<Reporte> reporteActivo = reporteRepository.findActiveReportByEquipo(reporteCreateDTO.getEquipoId(), 
                EstadoReporte.ABIERTO, EstadoReporte.EN_PROCESO);
        if (reporteActivo.isPresent()) {
            throw new IllegalArgumentException("El equipo ya tiene un reporte activo. Debe cerrarse antes de crear uno nuevo.");
        }
        
        Causa causa = causaRepository.findById(reporteCreateDTO.getCausaId())
                .orElseThrow(() -> new IllegalArgumentException("Causa no encontrada"));
        
        Equipo equipo = equipoRepository.findById(reporteCreateDTO.getEquipoId())
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado"));
        
        Usuario currentUser = authService.getCurrentUser();
        
        Reporte reporte = new Reporte();
        reporte.setObservacion(reporteCreateDTO.getObservacion());
        reporte.setIdCausa(causa);
        reporte.setIdEquipo(equipo);
        reporte.setIdUsuario(currentUser);
        reporte.setEstado(EstadoReporte.ABIERTO);
        reporte.setActivo(true);
        reporte.setFechaCommit(LocalDateTime.now());
        
        Reporte savedReporte = reporteRepository.save(reporte);
        return ReporteDTO.fromEntity(savedReporte);
    }
    
    @Override
    @Transactional
    public ReporteDTO update(Integer id, ReporteUpdateDTO reporteUpdateDTO) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reporte no encontrado"));
        
        // Solo se pueden editar reportes ABIERTOS
        if (reporte.getEstado() != EstadoReporte.ABIERTO) {
            throw new IllegalArgumentException("Solo se pueden editar reportes en estado ABIERTO");
        }
        
        // Validar transiciones de estado permitidas
        EstadoReporte nuevoEstado = reporteUpdateDTO.getEstado();
        if (!esTransicionValida(reporte.getEstado(), nuevoEstado)) {
            throw new IllegalArgumentException("Transición de estado no permitida: " + 
                reporte.getEstado() + " -> " + nuevoEstado);
        }
        
        Causa causa = causaRepository.findById(reporteUpdateDTO.getCausaId())
                .orElseThrow(() -> new IllegalArgumentException("Causa no encontrada"));
        
        Equipo equipo = equipoRepository.findById(reporteUpdateDTO.getEquipoId())
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado"));
        
        if (!reporte.getIdEquipo().getId().equals(reporteUpdateDTO.getEquipoId())) {
            Optional<Reporte> reporteActivoEquipo = reporteRepository.findActiveReportByEquipo(reporteUpdateDTO.getEquipoId(), 
                    EstadoReporte.ABIERTO, EstadoReporte.EN_PROCESO);
            if (reporteActivoEquipo.isPresent() && !reporteActivoEquipo.get().getId().equals(id)) {
                throw new IllegalArgumentException("El equipo seleccionado ya tiene un reporte activo");
            }
        }
        
        reporte.setObservacion(reporteUpdateDTO.getObservacion());
        reporte.setIdCausa(causa);
        reporte.setIdEquipo(equipo);
        reporte.setEstado(reporteUpdateDTO.getEstado());
        reporte.setFechaCommit(LocalDateTime.now());
        
        Reporte savedReporte = reporteRepository.save(reporte);
        return ReporteDTO.fromEntity(savedReporte);
    }
    
    @Override
    @Transactional
    public void delete(Integer id) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reporte no encontrado"));
        
        if (reporte.getEstado() != EstadoReporte.ABIERTO) {
            throw new IllegalArgumentException("Solo se pueden eliminar reportes en estado ABIERTO");
        }
        
        reporte.setActivo(false);
        reporte.setFechaCommit(LocalDateTime.now());
        reporteRepository.save(reporte);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean equipoTieneReportesAbiertos(Integer equipoId) {
        // Solo reportes ABIERTO y EN_PROCESO bloquean asignaciones
        // RESUELTO y CERRADO ya no bloquean porque están finalizados
        return reporteRepository.findActiveReportByEquipo(equipoId, 
                EstadoReporte.ABIERTO, EstadoReporte.EN_PROCESO).isPresent();
    }
    
    @Override
    @Transactional
    public ReporteDTO cambiarEstado(Integer id, EstadoReporte nuevoEstado) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reporte no encontrado"));
                
        if (!esTransicionValida(reporte.getEstado(), nuevoEstado)) {
            throw new IllegalArgumentException("Transición de estado no permitida: " + 
                reporte.getEstado() + " -> " + nuevoEstado);
        }
        
        reporte.setEstado(nuevoEstado);
        reporte.setFechaCommit(LocalDateTime.now());
        
        Reporte savedReporte = reporteRepository.save(reporte);
        return ReporteDTO.fromEntity(savedReporte);
    }
    
    private boolean esTransicionValida(EstadoReporte estadoActual, EstadoReporte nuevoEstado) {
        return switch (estadoActual) {
            case ABIERTO -> nuevoEstado == EstadoReporte.ABIERTO || nuevoEstado == EstadoReporte.EN_PROCESO;
            case EN_PROCESO -> nuevoEstado == EstadoReporte.EN_PROCESO || 
                              nuevoEstado == EstadoReporte.RESUELTO || 
                              nuevoEstado == EstadoReporte.CERRADO;
            case RESUELTO, CERRADO -> nuevoEstado == estadoActual; // No se pueden cambiar estados finales
        };
    }
}