package com.gestion.equipos.service;

import com.gestion.equipos.dto.UsuarioEmpleadoCreateDTO;
import com.gestion.equipos.dto.UsuarioEmpleadoDTO;
import com.gestion.equipos.dto.UsuarioEmpleadoUpdateDTO;
import com.gestion.equipos.entity.Usuario;
import com.gestion.equipos.entity.enums.EstadoUsuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    
    Page<Usuario> findAllWithFilters(String codigo, String usuario, String primerNombre, 
                                   String primerApellido, EstadoUsuario estado,
                                   LocalDate fechaIngresoDesde, LocalDate fechaIngresoHasta,
                                   Boolean isAdmin, Pageable pageable);
    
    Page<UsuarioEmpleadoDTO> findAllUsuarioEmpleadoWithFilters(String codigo, String usuario, String primerNombre,
                                                             String primerApellido, EstadoUsuario estado,
                                                             LocalDate fechaIngresoDesde, LocalDate fechaIngresoHasta,
                                                             Boolean isAdmin, Boolean esEmpleado, Integer areaId,
                                                             Pageable pageable);
    
    List<Usuario> findAll();
    
    List<UsuarioEmpleadoDTO> findAllUsuarioEmpleado();
    
    Optional<Usuario> findById(Integer id);
    
    Optional<UsuarioEmpleadoDTO> findUsuarioEmpleadoById(Integer usuarioId);
    
    Usuario save(Usuario usuario);
    
    UsuarioEmpleadoDTO saveUsuarioEmpleado(UsuarioEmpleadoCreateDTO createDTO);
    
    Usuario update(Integer id, Usuario usuario);
    
    UsuarioEmpleadoDTO updateUsuarioEmpleado(Integer usuarioId, UsuarioEmpleadoUpdateDTO updateDTO);
    
    void deleteById(Integer id);
    
    void deleteUsuarioEmpleado(Integer usuarioId);
    
    boolean existsByUsuario(String usuario);
    
    boolean existsByCodigo(String codigo);
    
    boolean existsByUsuarioAndIdNot(String usuario, Integer id);
    
    boolean existsByCodigoAndIdNot(String codigo, Integer id);
    
    List<com.gestion.equipos.dto.HistorialEmpleadoDTO> getHistorialEmpleado(Integer usuarioId);
}