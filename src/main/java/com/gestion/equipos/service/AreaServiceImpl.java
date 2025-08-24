package com.gestion.equipos.service;

import com.gestion.equipos.dto.AreaDTO;
import com.gestion.equipos.entity.Area;
import com.gestion.equipos.entity.QArea;
import com.gestion.equipos.entity.Usuario;
import com.gestion.equipos.entity.enums.EstadoUsuario;
import com.gestion.equipos.repository.AreaRepository;
import com.gestion.equipos.repository.UsuarioRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AreaServiceImpl implements AreaService {

    private final AreaRepository areaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpleadoService empleadoService;
    private final HistorialEmpleadoService historialEmpleadoService;

    @Override
    @Transactional(readOnly = true)
    public List<Area> findAll() {
        return areaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Area> findAllActive() {
        return areaRepository.findByActivoTrueOrderByNombre();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Area> findById(Integer id) {
        return areaRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Area> findAllWithFilters(String nombre, Boolean activo, Integer responsableId, Pageable pageable) {
        QArea qArea = QArea.area;
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(nombre)) {
            builder.and(qArea.nombre.containsIgnoreCase(nombre));
        }

        if (activo != null) {
            builder.and(qArea.activo.eq(activo));
        }

        if (responsableId != null) {
            builder.and(qArea.idUsuario.id.eq(responsableId));
        }

        return areaRepository.findAll(builder, pageable);
    }

    @Override
    public Area save(Area area) {
        validateAreaForCreate(area);
        area.setFechaCommit(LocalDateTime.now());
        return areaRepository.save(area);
    }

    @Override
    public Area update(Integer id, Area area) {
        validateAreaForUpdate(id, area);
        
        Area existingArea = areaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Área no encontrada con ID: " + id));
        
        updateAreaFields(existingArea, area);
        existingArea.setFechaCommit(LocalDateTime.now());
        
        return areaRepository.save(existingArea);
    }

    @Override
    public void deleteById(Integer id) {
        if (!areaRepository.existsById(id)) {
            throw new RuntimeException("Área no encontrada con ID: " + id);
        }
        
        if (hasEmpleadosActivos(id)) {
            throw new RuntimeException("No se puede eliminar el área porque tiene empleados activos asignados");
        }
        
        Area area = areaRepository.findById(id).orElseThrow();
        area.setActivo(false);
        areaRepository.save(area);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNombre(String nombre) {
        return areaRepository.existsByNombreIgnoreCase(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNombreAndIdNot(String nombre, Integer id) {
        return areaRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id);
    }

    @Override
    public void asignarResponsable(Integer areaId, Integer usuarioId) {
        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new RuntimeException("Área no encontrada"));
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                
        if (usuario.getEstado() != EstadoUsuario.ACTIVO) {
            throw new RuntimeException("El usuario debe estar activo para ser responsable de área");
        }
        
        // Si había un responsable anterior, registrar su descenso
        if (area.getIdUsuario() != null) {
            Optional<com.gestion.equipos.entity.Empleado> empleadoAnterior = 
                empleadoService.findByUsuario(area.getIdUsuario());
            if (empleadoAnterior.isPresent()) {
                historialEmpleadoService.registrarDescenso(
                    empleadoAnterior.get().getId(), 
                    empleadoAnterior.get().getCargo(),
                    "Removido como jefe de área: " + area.getNombre(),
                    null
                );
            }
        }
        
        // Verificar si el nuevo usuario es empleado para registrar ascenso
        Optional<com.gestion.equipos.entity.Empleado> empleadoNuevo = 
            empleadoService.findByUsuario(usuario);
        
        area.setIdUsuario(usuario);
        areaRepository.save(area);
        
        // Si el usuario es empleado, registrar ascenso
        if (empleadoNuevo.isPresent()) {
            historialEmpleadoService.registrarAscensoJefe(
                empleadoNuevo.get().getId(),
                area,
                "Jefe de Área",
                "Asignado como jefe de área: " + area.getNombre(),
                null
            );
        }
    }

    @Override
    public void removerResponsable(Integer areaId) {
        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new RuntimeException("Área no encontrada"));
        
        // Si había un responsable, registrar su descenso
        if (area.getIdUsuario() != null) {
            Optional<com.gestion.equipos.entity.Empleado> empleadoAnterior = 
                empleadoService.findByUsuario(area.getIdUsuario());
            if (empleadoAnterior.isPresent()) {
                historialEmpleadoService.registrarDescenso(
                    empleadoAnterior.get().getId(), 
                    empleadoAnterior.get().getCargo(),
                    "Removido como jefe de área: " + area.getNombre(),
                    null
                );
            }
        }
        
        area.setIdUsuario(null);
        areaRepository.save(area);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasEmpleadosActivos(Integer areaId) {
        return empleadoService.existsByAreaAndUsuarioActivo(areaId);
    }

    private void validateAreaForCreate(Area area) {
        if (existsByNombre(area.getNombre())) {
            throw new RuntimeException("Ya existe un área con el nombre: " + area.getNombre());
        }
    }

    private void validateAreaForUpdate(Integer id, Area area) {
        if (existsByNombreAndIdNot(area.getNombre(), id)) {
            throw new RuntimeException("Ya existe otra área con el nombre: " + area.getNombre());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AreaDTO> findAllAreaDTOWithFilters(String nombre, Boolean activo, Integer responsableId, Pageable pageable) {
        Page<Area> areasPage = findAllWithFilters(nombre, activo, responsableId, pageable);
        
        List<AreaDTO> areaDTOs = areasPage.getContent().stream()
                .map(this::mapToAreaDTO)
                .toList();
        
        return new PageImpl<>(areaDTOs, pageable, areasPage.getTotalElements());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<AreaDTO> findAreaDTOById(Integer id) {
        return areaRepository.findById(id)
                .map(this::mapToAreaDTO);
    }
    
    private AreaDTO mapToAreaDTO(Area area) {
        AreaDTO dto = new AreaDTO();
        dto.setId(area.getId());
        dto.setNombre(area.getNombre());
        dto.setActivo(area.getActivo());
        dto.setFechaCommit(area.getFechaCommit());
        
        // Mapear responsable
        if (area.getIdUsuario() != null) {
            dto.setResponsableId(area.getIdUsuario().getId());
            dto.setResponsableNombre(area.getIdUsuario().getPrimerNombre() + " " + area.getIdUsuario().getPrimerApellido());
        }
        
        // Mapear creado por
        if (area.getCreadoPor() != null) {
            dto.setCreadoPorId(area.getCreadoPor().getId());
            dto.setCreadoPorNombre(area.getCreadoPor().getPrimerNombre() + " " + area.getCreadoPor().getPrimerApellido());
        }
        
        return dto;
    }

    private void updateAreaFields(Area existing, Area updated) {
        existing.setNombre(updated.getNombre());
        existing.setActivo(updated.getActivo());
        existing.setIdUsuario(updated.getIdUsuario());
        existing.setCreadoPor(updated.getCreadoPor());
    }
}