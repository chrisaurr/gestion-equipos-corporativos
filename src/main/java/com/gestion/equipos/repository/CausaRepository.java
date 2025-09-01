package com.gestion.equipos.repository;

import com.gestion.equipos.entity.Causa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CausaRepository extends JpaRepository<Causa, Integer>, QuerydslPredicateExecutor<Causa> {
    
    List<Causa> findByActivoTrueOrderByNombreAsc();
    
    @Query("SELECT c FROM Causa c WHERE c.nombre = :nombre AND c.activo = true")
    Optional<Causa> findByNombreAndActivoTrue(@Param("nombre") String nombre);
    
    @Query("SELECT c FROM Causa c WHERE LOWER(c.nombre) = LOWER(:nombre)")
    Optional<Causa> findByNombreIgnoreCase(@Param("nombre") String nombre);
    
    @Query("SELECT c FROM Causa c WHERE LOWER(c.nombre) = LOWER(:nombre) AND c.id != :id")
    Optional<Causa> findByNombreIgnoreCaseAndIdNot(@Param("nombre") String nombre, @Param("id") Integer id);
    
    @Query("SELECT c FROM Causa c WHERE LOWER(c.nombre) = LOWER(:nombre) AND c.activo = true")
    Optional<Causa> findByNombreIgnoreCaseAndActivoTrue(@Param("nombre") String nombre);
    
    @Query("SELECT c FROM Causa c WHERE LOWER(c.nombre) = LOWER(:nombre) AND c.activo = false")
    Optional<Causa> findByNombreIgnoreCaseAndActivoFalse(@Param("nombre") String nombre);
}