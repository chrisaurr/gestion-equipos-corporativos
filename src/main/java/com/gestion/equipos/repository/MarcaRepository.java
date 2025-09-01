package com.gestion.equipos.repository;

import com.gestion.equipos.entity.Marca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Integer>,
        QuerydslPredicateExecutor<Marca> {
    
    List<Marca> findByActivoTrueOrderByNombreAsc();
    
    @Query("SELECT m FROM Marca m WHERE LOWER(m.nombre) = LOWER(:nombre)")
    Optional<Marca> findByNombreIgnoreCase(@Param("nombre") String nombre);
    
    @Query("SELECT m FROM Marca m WHERE LOWER(m.nombre) = LOWER(:nombre) AND m.id != :id")
    Optional<Marca> findByNombreIgnoreCaseAndIdNot(@Param("nombre") String nombre, @Param("id") Integer id);
    
    @Query("SELECT m FROM Marca m WHERE LOWER(m.nombre) = LOWER(:nombre) AND m.activo = true")
    Optional<Marca> findByNombreIgnoreCaseAndActivoTrue(@Param("nombre") String nombre);
    
    @Query("SELECT m FROM Marca m WHERE LOWER(m.nombre) = LOWER(:nombre) AND m.activo = false")
    Optional<Marca> findByNombreIgnoreCaseAndActivoFalse(@Param("nombre") String nombre);
}