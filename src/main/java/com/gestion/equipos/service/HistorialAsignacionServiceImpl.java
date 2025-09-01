package com.gestion.equipos.service;

import com.gestion.equipos.entity.HistorialAsignacion;
import com.gestion.equipos.entity.enums.EstadoDevolucion;
import com.gestion.equipos.repository.HistorialAsignacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HistorialAsignacionServiceImpl implements HistorialAsignacionService {
    
    private final HistorialAsignacionRepository historialAsignacionRepository;
    private final ReporteService reporteService;
    
    @Override
    @Transactional(readOnly = true)
    public List<com.gestion.equipos.dto.HistorialAsignacionDTO> findHistorialByEquipo(Integer equipoId) {
        var historiales = historialAsignacionRepository.findHistorialWithUsersDataByEquipoId(equipoId);
        return historiales.stream()
                .map(com.gestion.equipos.dto.HistorialAsignacionDTO::fromEntity)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByIdEquipo_Id(Integer equipoId) {
        return historialAsignacionRepository.existsByIdEquipo_Id(equipoId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<HistorialAsignacion> findPendingReturnByEquipo(Integer equipoId) {
        return historialAsignacionRepository.findByIdEquipo_IdAndEstadoDevolucion(equipoId, EstadoDevolucion.PENDIENTE);
    }
    
    
    @Override
    @Transactional
    public void confirmarDevolucion(Integer historialId) {
        HistorialAsignacion historial = historialAsignacionRepository.findById(historialId)
                .orElseThrow(() -> new RuntimeException("Historial de asignación no encontrado"));
        
        if (historial.getEstadoDevolucion() != EstadoDevolucion.PENDIENTE) {
            throw new RuntimeException("Solo se pueden confirmar devoluciones en estado PENDIENTE");
        }
        
        historial.setEstadoDevolucion(EstadoDevolucion.CONFIRMADA);
        historialAsignacionRepository.save(historial);
        
        if (historial.getIdEquipo().getIdUsuario() != null) {
            historial.getIdEquipo().setIdUsuario(null);
        }
    }
    
}