package com.gestion.equipos.repository;

import com.gestion.equipos.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AreaRepository extends JpaRepository<Area, Integer>, QuerydslPredicateExecutor<Area> {
    
    List<Area> findByActivoTrueOrderByNombre();
    
    boolean existsByNombreAndIdNot(String nombre, Integer id);
    
    boolean existsByNombre(String nombre);
    
    boolean existsByNombreIgnoreCase(String nombre);
    
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Integer id);
}