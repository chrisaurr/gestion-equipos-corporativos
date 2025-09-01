package com.gestion.equipos.repository;

import com.gestion.equipos.entity.Equipo;
import com.gestion.equipos.entity.Herramienta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HerramientaRepository extends JpaRepository<Herramienta, Integer>, QuerydslPredicateExecutor<Herramienta> {
    
    Optional<Herramienta> findByIdEquipo_Id(Integer equipoId);
    
    Optional<Herramienta> findByIdEquipo(Equipo equipo);
    
    void deleteByIdEquipo_Id(Integer equipoId);
}