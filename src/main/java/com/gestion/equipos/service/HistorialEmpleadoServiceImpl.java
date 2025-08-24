package com.gestion.equipos.service;

import com.gestion.equipos.entity.*;
import com.gestion.equipos.entity.enums.TipoCambioEmpleado;
import com.gestion.equipos.repository.EmpleadoRepository;
import com.gestion.equipos.repository.HistorialEmpleadoRepository;
import com.gestion.equipos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class HistorialEmpleadoServiceImpl implements HistorialEmpleadoService {

    private final HistorialEmpleadoRepository historialRepository;
    private final EmpleadoRepository empleadoRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public HistorialEmpleado save(HistorialEmpleado historial) {
        if (historial.getFechaCambio() == null) {
            historial.setFechaCambio(LocalDateTime.now());
        }
        return historialRepository.save(historial);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HistorialEmpleado> findById(Integer id) {
        return historialRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistorialEmpleado> findByEmpleado(Empleado empleado) {
        return historialRepository.findByEmpleadoOrderByFechaCambioDesc(empleado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistorialEmpleado> findByEmpleadoId(Integer empleadoId) {
        return historialRepository.findByEmpleado_IdOrderByFechaCambioDesc(empleadoId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HistorialEmpleado> findByEmpleado(Empleado empleado, Pageable pageable) {
        return historialRepository.findByEmpleadoOrderByFechaCambioDesc(empleado, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HistorialEmpleado> findByEmpleadoId(Integer empleadoId, Pageable pageable) {
        return historialRepository.findByEmpleado_IdOrderByFechaCambioDesc(empleadoId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistorialEmpleado> findByTipoCambio(TipoCambioEmpleado tipoCambio) {
        return historialRepository.findByTipoCambioOrderByFechaCambioDesc(tipoCambio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistorialEmpleado> findByFechaRange(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return historialRepository.findByFechaCambioBetweenOrderByFechaCambioDesc(fechaInicio, fechaFin);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByEmpleadoId(Integer empleadoId) {
        return historialRepository.countByEmpleado_Id(empleadoId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByTipoCambio(TipoCambioEmpleado tipoCambio) {
        return historialRepository.countByTipoCambio(tipoCambio);
    }

    @Override
    @Transactional
    public void registrarAscensoJefe(Integer empleadoId, Area area, String nuevoCargo, 
                                   String motivo, Integer creadoPorId) {
        
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        
        Usuario creadoPor = null;
        if (creadoPorId != null) {
            creadoPor = usuarioRepository.findById(creadoPorId).orElse(null);
        }

        // Crear registro de historial
        HistorialEmpleado historial = new HistorialEmpleado();
        historial.setEmpleado(empleado);
        historial.setAreaAnterior(empleado.getIdArea());
        historial.setAreaNueva(area);
        historial.setCargoAnterior(empleado.getCargo());
        historial.setCargoNuevo(nuevoCargo);
        historial.setTipoCambio(TipoCambioEmpleado.ASCENSO_JEFE);
        historial.setMotivo(motivo != null ? motivo : "Ascenso a jefe de área");
        historial.setCreadoPor(creadoPor);
        historial.setFechaCambio(LocalDateTime.now());

        save(historial);
    }

    @Override
    @Transactional
    public void registrarDescenso(Integer empleadoId, String nuevoCargo, 
                                String motivo, Integer creadoPorId) {
        
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        
        Usuario creadoPor = null;
        if (creadoPorId != null) {
            creadoPor = usuarioRepository.findById(creadoPorId).orElse(null);
        }

        // Crear registro de historial
        HistorialEmpleado historial = new HistorialEmpleado();
        historial.setEmpleado(empleado);
        historial.setAreaAnterior(empleado.getIdArea());
        historial.setAreaNueva(empleado.getIdArea()); // Misma área
        historial.setCargoAnterior(empleado.getCargo());
        historial.setCargoNuevo(nuevoCargo);
        historial.setTipoCambio(TipoCambioEmpleado.DESCENSO);
        historial.setMotivo(motivo != null ? motivo : "Descenso de cargo");
        historial.setCreadoPor(creadoPor);
        historial.setFechaCambio(LocalDateTime.now());

        save(historial);
    }
}