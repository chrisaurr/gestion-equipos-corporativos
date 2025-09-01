package com.gestion.equipos.repository;

import com.gestion.equipos.entity.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Integer>,
        QuerydslPredicateExecutor<Ubicacion> {
    
    List<Ubicacion> findByActivoTrueOrderByNombreAsc();
    
    @Query("SELECT u FROM Ubicacion u WHERE LOWER(u.nombre) = LOWER(:nombre)")
    Optional<Ubicacion> findByNombreIgnoreCase(@Param("nombre") String nombre);
    
    @Query("SELECT u FROM Ubicacion u WHERE LOWER(u.nombre) = LOWER(:nombre) AND u.id != :id")
    Optional<Ubicacion> findByNombreIgnoreCaseAndIdNot(@Param("nombre") String nombre, @Param("id") Integer id);
    
    @Query("SELECT u FROM Ubicacion u WHERE LOWER(u.nombre) = LOWER(:nombre) AND u.activo = true")
    Optional<Ubicacion> findByNombreIgnoreCaseAndActivoTrue(@Param("nombre") String nombre);
    
    @Query("SELECT u FROM Ubicacion u WHERE LOWER(u.nombre) = LOWER(:nombre) AND u.activo = false")
    Optional<Ubicacion> findByNombreIgnoreCaseAndActivoFalse(@Param("nombre") String nombre);
}