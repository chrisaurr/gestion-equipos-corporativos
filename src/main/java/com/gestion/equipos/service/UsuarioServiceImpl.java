package com.gestion.equipos.service;

import com.gestion.equipos.dto.UsuarioEmpleadoCreateDTO;
import com.gestion.equipos.dto.UsuarioEmpleadoDTO;
import com.gestion.equipos.dto.UsuarioEmpleadoUpdateDTO;
import com.gestion.equipos.entity.*;
import com.gestion.equipos.entity.enums.EstadoUsuario;
import com.gestion.equipos.repository.UsuarioRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmpleadoService empleadoService;
    private final AreaService areaService;
    private final HistorialEmpleadoService historialEmpleadoService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Page<Usuario> findAllWithFilters(String codigo, String usuario, String primerNombre,
                                          String primerApellido, EstadoUsuario estado,
                                          LocalDate fechaIngresoDesde, LocalDate fechaIngresoHasta,
                                          Boolean isAdmin, Pageable pageable) {
        
        QUsuario qUsuario = QUsuario.usuario1;
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(codigo)) {
            builder.and(qUsuario.codigo.containsIgnoreCase(codigo));
        }

        if (StringUtils.hasText(usuario)) {
            builder.and(qUsuario.usuario.containsIgnoreCase(usuario));
        }

        if (StringUtils.hasText(primerNombre)) {
            builder.and(qUsuario.primerNombre.containsIgnoreCase(primerNombre));
        }

        if (StringUtils.hasText(primerApellido)) {
            builder.and(qUsuario.primerApellido.containsIgnoreCase(primerApellido));
        }

        if (estado != null) {
            builder.and(qUsuario.estado.eq(estado));
        }

        if (fechaIngresoDesde != null) {
            builder.and(qUsuario.fechaIngreso.goe(fechaIngresoDesde));
        }

        if (fechaIngresoHasta != null) {
            builder.and(qUsuario.fechaIngreso.loe(fechaIngresoHasta));
        }

        if (isAdmin != null) {
            builder.and(qUsuario.isAdmin.eq(isAdmin));
        }

        return usuarioRepository.findAll(builder, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findById(Integer id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Usuario save(Usuario usuario) {
        validateUsuarioForCreate(usuario);
        
        // Encriptar contraseña con BCrypt
        if (usuario.getPassword() != null) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        
        usuario.setFechaCommit(LocalDateTime.now());
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario update(Integer id, Usuario usuario) {
        validateUsuarioForUpdate(id, usuario);
        
        Usuario existingUsuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        
        updateUsuarioFields(existingUsuario, usuario);
        existingUsuario.setFechaCommit(LocalDateTime.now());
        
        return usuarioRepository.save(existingUsuario);
    }

    @Override
    public void deleteById(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsuario(String usuario) {
        return usuarioRepository.existsByUsuario(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCodigo(String codigo) {
        return usuarioRepository.existsByCodigo(codigo);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsuarioAndIdNot(String usuario, Integer id) {
        return usuarioRepository.existsByUsuarioAndIdNot(usuario, id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCodigoAndIdNot(String codigo, Integer id) {
        return usuarioRepository.existsByCodigoAndIdNot(codigo, id);
    }

    private void validateUsuarioForCreate(Usuario usuario) {
        if (existsByUsuario(usuario.getUsuario())) {
            throw new RuntimeException("Ya existe un usuario con el nombre: " + usuario.getUsuario());
        }
        
        if (existsByCodigo(usuario.getCodigo())) {
            throw new RuntimeException("Ya existe un usuario con el código: " + usuario.getCodigo());
        }
    }

    private void validateUsuarioForUpdate(Integer id, Usuario usuario) {
        if (existsByUsuarioAndIdNot(usuario.getUsuario(), id)) {
            throw new RuntimeException("Ya existe otro usuario con el nombre: " + usuario.getUsuario());
        }
        
        if (existsByCodigoAndIdNot(usuario.getCodigo(), id)) {
            throw new RuntimeException("Ya existe otro usuario con el código: " + usuario.getCodigo());
        }
    }

    private void updateUsuarioFields(Usuario existing, Usuario updated) {
        existing.setCodigo(updated.getCodigo());
        existing.setUsuario(updated.getUsuario());
        existing.setPrimerNombre(updated.getPrimerNombre());
        existing.setSegundoNombre(updated.getSegundoNombre());
        existing.setPrimerApellido(updated.getPrimerApellido());
        existing.setSegundoApellido(updated.getSegundoApellido());
        existing.setEstado(updated.getEstado());
        existing.setFechaIngreso(updated.getFechaIngreso());
        existing.setFechaSalida(updated.getFechaSalida());
        existing.setIsAdmin(updated.getIsAdmin());
        
        // Solo actualizar contraseña si se proporciona una nueva
        if (updated.getPassword() != null && !updated.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(updated.getPassword()));
        }
    }

    // ===== MÉTODOS USUARIO+EMPLEADO =====

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioEmpleadoDTO> findAllUsuarioEmpleadoWithFilters(String codigo, String usuario, String primerNombre,
                                                                   String primerApellido, EstadoUsuario estado,
                                                                   LocalDate fechaIngresoDesde, LocalDate fechaIngresoHasta,
                                                                   Boolean isAdmin, Boolean esEmpleado, Integer areaId,
                                                                   Pageable pageable) {
        
        Page<Usuario> usuariosPage = findAllWithFilters(
                codigo, usuario, primerNombre, primerApellido, estado,
                fechaIngresoDesde, fechaIngresoHasta, isAdmin, pageable
        );
        
        List<UsuarioEmpleadoDTO> usuarioEmpleadoDTOs = usuariosPage.getContent().stream()
                .map(this::mapToUsuarioEmpleadoDTO)
                .filter(dto -> {
                    if (esEmpleado != null) {
                        return esEmpleado.equals(dto.getEsEmpleado());
                    }
                    return true;
                })
                .filter(dto -> {
                    if (areaId != null && dto.getEsEmpleado()) {
                        return areaId.equals(dto.getAreaId());
                    }
                    return true;
                })
                .collect(Collectors.toList());
        
        return new PageImpl<>(usuarioEmpleadoDTOs, pageable, usuariosPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioEmpleadoDTO> findAllUsuarioEmpleado() {
        return findAll().stream()
                .map(this::mapToUsuarioEmpleadoDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsuarioEmpleadoDTO> findUsuarioEmpleadoById(Integer usuarioId) {
        return findById(usuarioId)
                .map(this::mapToUsuarioEmpleadoDTO);
    }

    @Override
    public UsuarioEmpleadoDTO saveUsuarioEmpleado(UsuarioEmpleadoCreateDTO createDTO) {
        validateUsuarioEmpleadoForCreate(createDTO);
        
        Usuario usuario = modelMapper.map(createDTO, Usuario.class);
        usuario.setFechaCommit(LocalDateTime.now());
        Usuario savedUsuario = save(usuario);
        
        if (createDTO.getEsEmpleado()) {
            if (createDTO.getAreaId() == null) {
                throw new RuntimeException("El área es obligatoria para empleados");
            }
            
            Area area = areaService.findById(createDTO.getAreaId())
                    .orElseThrow(() -> new RuntimeException("Área no encontrada"));
            
            Empleado empleado = new Empleado();
            empleado.setIdUsuario(savedUsuario);
            empleado.setIdArea(area);
            empleado.setCargo(createDTO.getCargo() != null ? createDTO.getCargo() : "");
            empleado.setObservaciones(createDTO.getObservaciones());
            
            empleadoService.save(empleado);
        }
        
        return mapToUsuarioEmpleadoDTO(savedUsuario);
    }

    @Override
    public UsuarioEmpleadoDTO updateUsuarioEmpleado(Integer usuarioId, UsuarioEmpleadoUpdateDTO updateDTO) {
        validateUsuarioEmpleadoForUpdate(usuarioId, updateDTO);
        
        Usuario usuario = findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        updateUsuarioFieldsFromDTO(usuario, updateDTO);
        Usuario updatedUsuario = update(usuarioId, usuario);
        
        handleEmpleadoUpdate(updatedUsuario, updateDTO);
        
        return mapToUsuarioEmpleadoDTO(updatedUsuario);
    }

    @Override
    public void deleteUsuarioEmpleado(Integer usuarioId) {
        Usuario usuario = findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Solo desactivar el usuario - NO eliminar empleado para mantener historial
        usuario.setEstado(EstadoUsuario.INACTIVO);
        update(usuarioId, usuario);
    }

    private void validateUsuarioEmpleadoForCreate(UsuarioEmpleadoCreateDTO createDTO) {
        if (existsByUsuario(createDTO.getUsuario())) {
            throw new RuntimeException("Ya existe un usuario con el nombre: " + createDTO.getUsuario());
        }
        
        if (existsByCodigo(createDTO.getCodigo())) {
            throw new RuntimeException("Ya existe un usuario con el código: " + createDTO.getCodigo());
        }
        
        if (createDTO.getEsEmpleado() && createDTO.getAreaId() == null) {
            throw new RuntimeException("El área es obligatoria para empleados");
        }
        
        if (createDTO.getEsEmpleado() && (createDTO.getCargo() == null || createDTO.getCargo().trim().isEmpty())) {
            throw new RuntimeException("El cargo es obligatorio para empleados");
        }
    }

    private void validateUsuarioEmpleadoForUpdate(Integer usuarioId, UsuarioEmpleadoUpdateDTO updateDTO) {
        if (existsByUsuarioAndIdNot(updateDTO.getUsuario(), usuarioId)) {
            throw new RuntimeException("Ya existe otro usuario con el nombre: " + updateDTO.getUsuario());
        }
        
        if (existsByCodigoAndIdNot(updateDTO.getCodigo(), usuarioId)) {
            throw new RuntimeException("Ya existe otro usuario con el código: " + updateDTO.getCodigo());
        }
        
        if (updateDTO.getEsEmpleado() && updateDTO.getAreaId() == null) {
            throw new RuntimeException("El área es obligatoria para empleados");
        }
        
        if (updateDTO.getEsEmpleado() && (updateDTO.getCargo() == null || updateDTO.getCargo().trim().isEmpty())) {
            throw new RuntimeException("El cargo es obligatorio para empleados");
        }
    }

    private void updateUsuarioFieldsFromDTO(Usuario usuario, UsuarioEmpleadoUpdateDTO updateDTO) {
        usuario.setCodigo(updateDTO.getCodigo());
        usuario.setUsuario(updateDTO.getUsuario());
        usuario.setPrimerNombre(updateDTO.getPrimerNombre());
        usuario.setSegundoNombre(updateDTO.getSegundoNombre());
        usuario.setPrimerApellido(updateDTO.getPrimerApellido());
        usuario.setSegundoApellido(updateDTO.getSegundoApellido());
        usuario.setEstado(updateDTO.getEstado());
        usuario.setFechaIngreso(updateDTO.getFechaIngreso());
        usuario.setFechaSalida(updateDTO.getFechaSalida());
        usuario.setIsAdmin(updateDTO.getIsAdmin());
        
        if (updateDTO.isChangePassword() && updateDTO.getPassword() != null) {
            usuario.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
        }
    }

    private void handleEmpleadoUpdate(Usuario usuario, UsuarioEmpleadoUpdateDTO updateDTO) {
        Optional<Empleado> existingEmpleado = empleadoService.findByUsuario(usuario);
        
        if (updateDTO.getEsEmpleado()) {
            Area area = areaService.findById(updateDTO.getAreaId())
                    .orElseThrow(() -> new RuntimeException("Área no encontrada"));
            
            if (existingEmpleado.isPresent()) {
                Empleado empleado = existingEmpleado.get();
                empleado.setIdArea(area);
                empleado.setCargo(updateDTO.getCargo());
                empleado.setObservaciones(updateDTO.getObservaciones());
                empleadoService.save(empleado);
            } else {
                Empleado nuevoEmpleado = new Empleado();
                nuevoEmpleado.setIdUsuario(usuario);
                nuevoEmpleado.setIdArea(area);
                nuevoEmpleado.setCargo(updateDTO.getCargo());
                nuevoEmpleado.setObservaciones(updateDTO.getObservaciones());
                empleadoService.save(nuevoEmpleado);
            }
        } else {
            existingEmpleado.ifPresent(empleado -> empleadoService.deleteByUsuario(usuario));
        }
    }

    private UsuarioEmpleadoDTO mapToUsuarioEmpleadoDTO(Usuario usuario) {
        UsuarioEmpleadoDTO dto = new UsuarioEmpleadoDTO();
        
        dto.setUsuarioId(usuario.getId());
        dto.setCodigo(usuario.getCodigo());
        dto.setUsuario(usuario.getUsuario());
        dto.setPrimerNombre(usuario.getPrimerNombre());
        dto.setSegundoNombre(usuario.getSegundoNombre());
        dto.setPrimerApellido(usuario.getPrimerApellido());
        dto.setSegundoApellido(usuario.getSegundoApellido());
        dto.setEstado(usuario.getEstado());
        dto.setFechaIngreso(usuario.getFechaIngreso());
        dto.setFechaSalida(usuario.getFechaSalida());
        dto.setIsAdmin(usuario.getIsAdmin());
        dto.setFechaCommit(usuario.getFechaCommit());
        
        Optional<Empleado> empleado = empleadoService.findByUsuario(usuario);
        if (empleado.isPresent()) {
            dto.setEmpleadoId(empleado.get().getId());
            dto.setAreaId(empleado.get().getIdArea().getId());
            dto.setAreaNombre(empleado.get().getIdArea().getNombre());
            dto.setCargo(empleado.get().getCargo());
            dto.setObservaciones(empleado.get().getObservaciones());
            dto.setEsEmpleado(true);
        } else {
            dto.setEsEmpleado(false);
        }
        
        return dto;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<com.gestion.equipos.dto.HistorialEmpleadoDTO> getHistorialEmpleado(Integer usuarioId) {
        // Buscar el empleado por usuario
        Optional<Empleado> empleado = empleadoService.findByUsuarioId(usuarioId);
        
        if (empleado.isEmpty()) {
            throw new RuntimeException("El usuario no es empleado");
        }
        
        // Obtener historial del empleado y convertir a DTOs
        List<HistorialEmpleado> historial = historialEmpleadoService.findByEmpleadoId(empleado.get().getId());
        
        return historial.stream()
                .map(this::mapToHistorialEmpleadoDTO)
                .collect(Collectors.toList());
    }
    
    private com.gestion.equipos.dto.HistorialEmpleadoDTO mapToHistorialEmpleadoDTO(HistorialEmpleado historial) {
        com.gestion.equipos.dto.HistorialEmpleadoDTO dto = new com.gestion.equipos.dto.HistorialEmpleadoDTO();
        
        dto.setId(historial.getId());
        dto.setTipoCambio(historial.getTipoCambio());
        dto.setCargoAnterior(historial.getCargoAnterior());
        dto.setCargoNuevo(historial.getCargoNuevo());
        dto.setMotivo(historial.getMotivo());
        dto.setFechaCambio(historial.getFechaCambio());
        
        // Manejar área anterior (puede ser null)
        if (historial.getAreaAnterior() != null) {
            dto.setAreaAnteriorId(historial.getAreaAnterior().getId());
            dto.setAreaAnteriorNombre(historial.getAreaAnterior().getNombre());
        }
        
        // Manejar área nueva (puede ser null)
        if (historial.getAreaNueva() != null) {
            dto.setAreaNuevaId(historial.getAreaNueva().getId());
            dto.setAreaNuevaNombre(historial.getAreaNueva().getNombre());
        }
        
        // Manejar creado por (puede ser null)
        if (historial.getCreadoPor() != null) {
            dto.setCreadoPor(historial.getCreadoPor().getPrimerNombre() + " " + historial.getCreadoPor().getPrimerApellido());
        }
        
        return dto;
    }
}