package com.gestion.equipos.service;

import com.gestion.equipos.dto.EquipoDTO;
import com.gestion.equipos.entity.Equipo;
import com.gestion.equipos.entity.HistorialAsignacion;
import com.gestion.equipos.entity.Usuario;
import com.gestion.equipos.entity.enums.EstadoDevolucion;
import com.gestion.equipos.entity.enums.EstadoEquipo;
import com.gestion.equipos.entity.enums.MotivoCambio;
import com.gestion.equipos.repository.EquipoRepository;
import com.gestion.equipos.repository.HistorialAsignacionRepository;
import com.gestion.equipos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EquipoAsignacionServiceImpl implements EquipoAsignacionService {
    
    private final EquipoRepository equipoRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistorialAsignacionRepository historialAsignacionRepository;
    private final EquipoService equipoService;
    private final AuthService authService;
    private final ReporteService reporteService;
    
    @Override
    @Transactional
    public EquipoDTO asignarEquipo(Integer equipoId, Integer usuarioId, String motivo) {
        boolean esAsignacionInicial = !historialAsignacionRepository.existsByIdEquipo_Id(equipoId);
        
        if (esAsignacionInicial) {
            return asignarEquipoConMotivo(equipoId, usuarioId, MotivoCambio.ASIGNACION_INICIAL, motivo);
        } else {
            return asignarEquipoConMotivo(equipoId, usuarioId, MotivoCambio.REASIGNACION_ADMINISTRATIVA, motivo);
        }
    }
    
    @Override
    @Transactional
    public EquipoDTO asignarEquipoConMotivo(Integer equipoId, Integer usuarioId, MotivoCambio motivoCambio, String motivo) {
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
        
        // Verificar si hay devoluciones pendientes
        Optional<HistorialAsignacion> pendingReturn = historialAsignacionRepository
                .findByIdEquipo_IdAndEstadoDevolucion(equipoId, EstadoDevolucion.PENDIENTE);
        if (pendingReturn.isPresent()) {
            throw new RuntimeException("No se puede asignar el equipo: tiene una devolución pendiente de confirmación");
        }
        
        
        // Verificar si hay reportes abiertos (ABIERTO/EN_PROCESO)
        if (reporteService.equipoTieneReportesAbiertos(equipoId)) {
            throw new RuntimeException("No se puede asignar el equipo: tiene reportes abiertos que deben resolverse primero");
        }
        
        if (equipo.getIdUsuario() != null) {
            throw new RuntimeException("El equipo ya está asignado");
        }
        
        if (equipo.getEstado() != EstadoEquipo.ACTIVO) {
            throw new RuntimeException("Solo se pueden asignar equipos en estado ACTIVO");
        }
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Usuario currentUser = authService.getCurrentUser();
        
        // Cerrar asignación activa si existe
        Optional<HistorialAsignacion> activeAssignment = historialAsignacionRepository
                .findActiveAssignmentByEquipo(equipoId);
        if (activeAssignment.isPresent()) {
            HistorialAsignacion historial = activeAssignment.get();
            historial.setFechaDevolucion(LocalDateTime.now());
            historial.setEstadoDevolucion(EstadoDevolucion.CONFIRMADA);
            historialAsignacionRepository.save(historial);
        }
        
        equipo.setIdUsuario(usuario);
        equipoRepository.save(equipo);
        
        HistorialAsignacion nuevoHistorial = new HistorialAsignacion();
        nuevoHistorial.setIdEquipo(equipo);
        nuevoHistorial.setIdUsuarioAnterior(null);
        nuevoHistorial.setIdUsuarioNuevo(usuario);
        nuevoHistorial.setFechaAsignacion(LocalDateTime.now());
        nuevoHistorial.setMotivoCambio(motivoCambio);
        nuevoHistorial.setObservaciones(motivo);
        nuevoHistorial.setEstadoDevolucion(null);
        nuevoHistorial.setAsignadoPor(currentUser);
        
        historialAsignacionRepository.save(nuevoHistorial);
        
        return equipoService.convertToDTO(equipo);
    }
    
    @Override
    @Transactional
    public EquipoDTO liberarEquipo(Integer equipoId, MotivoCambio motivoCambio, String motivo) {
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
        
        if (equipo.getIdUsuario() == null) {
            throw new RuntimeException("El equipo no está asignado");
        }
        
        // Verificar si hay reportes abiertos (ABIERTO/EN_PROCESO) - BLOQUEAN liberación
        if (reporteService.equipoTieneReportesAbiertos(equipoId)) {
            throw new RuntimeException("No se puede liberar el equipo: tiene reportes abiertos que deben resolverse primero");
        }
        
        Usuario currentUser = authService.getCurrentUser();
        
        Optional<HistorialAsignacion> activeAssignment = historialAsignacionRepository
                .findActiveAssignmentByEquipo(equipoId);
        
        if (activeAssignment.isPresent()) {
            HistorialAsignacion historial = activeAssignment.get();
            historial.setFechaDevolucion(LocalDateTime.now());
            historial.setEstadoDevolucion(EstadoDevolucion.PENDIENTE);
            historial.setMotivoCambio(motivoCambio);
            historial.setObservaciones(motivo);
            historialAsignacionRepository.save(historial);
        } else {
            throw new RuntimeException("No se encontró asignación activa para este equipo");
        }
        
        return equipoService.convertToDTO(equipo);
    }
    
}