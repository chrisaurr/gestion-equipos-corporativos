package com.gestion.equipos.repository;

import com.gestion.equipos.entity.Equipo;
import com.gestion.equipos.entity.Mobiliario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MobiliarioRepository extends JpaRepository<Mobiliario, Integer>, QuerydslPredicateExecutor<Mobiliario> {
    
    Optional<Mobiliario> findByIdEquipo_Id(Integer equipoId);
    
    Optional<Mobiliario> findByIdEquipo(Equipo equipo);
    
    void deleteByIdEquipo_Id(Integer equipoId);
}