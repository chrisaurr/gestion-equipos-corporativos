package com.gestion.equipos.repository;

import com.gestion.equipos.entity.Empleado;
import com.gestion.equipos.entity.Usuario;
import com.gestion.equipos.entity.enums.EstadoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {
    
    Optional<Empleado> findByIdUsuario(Usuario usuario);
    
    Optional<Empleado> findByIdUsuario_Id(Integer usuarioId);
    
    boolean existsByIdUsuario(Usuario usuario);
    
    boolean existsByIdUsuario_Id(Integer usuarioId);
    
    boolean existsByIdArea_IdAndIdUsuario_Estado(Integer areaId, EstadoUsuario estado);
    
    void deleteByIdUsuario(Usuario usuario);
    
    void deleteByIdUsuario_Id(Integer usuarioId);
}