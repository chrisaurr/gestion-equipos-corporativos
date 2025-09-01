package com.gestion.equipos.repository;

import com.gestion.equipos.entity.Usuario;
import com.gestion.equipos.entity.enums.EstadoUsuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer>,
        QuerydslPredicateExecutor<Usuario> {

    Page<Usuario> findByEstadoOrderByFechaCommitDesc(EstadoUsuario estado, Pageable pageable);
    
    List<Usuario> findByEstado(EstadoUsuario estado);
    
    boolean existsByUsuarioAndIdNot(String usuario, Integer id);
    
    boolean existsByCodigoAndIdNot(String codigo, Integer id);
    
    boolean existsByUsuario(String usuario);
    
    boolean existsByCodigo(String codigo);
    
    Optional<Usuario> findByUsuario(String usuario);
}