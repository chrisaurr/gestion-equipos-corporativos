package com.gestion.equipos.service;

import com.gestion.equipos.entity.Empleado;
import com.gestion.equipos.entity.Usuario;

import java.util.Optional;

public interface EmpleadoService {
    
    Optional<Empleado> findByUsuario(Usuario usuario);
    
    Optional<Empleado> findByUsuarioId(Integer usuarioId);
    
    Empleado save(Empleado empleado);
    
    void deleteByUsuario(Usuario usuario);
    
    void deleteByUsuarioId(Integer usuarioId);
    
    boolean existsByUsuario(Usuario usuario);
    
    boolean existsByUsuarioId(Integer usuarioId);
    
    boolean existsByAreaAndUsuarioActivo(Integer areaId);
}