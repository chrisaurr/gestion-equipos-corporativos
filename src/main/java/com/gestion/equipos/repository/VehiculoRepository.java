package com.gestion.equipos.repository;

import com.gestion.equipos.entity.Equipo;
import com.gestion.equipos.entity.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Integer>, QuerydslPredicateExecutor<Vehiculo> {
    
    Optional<Vehiculo> findByIdEquipo_Id(Integer equipoId);
    
    Optional<Vehiculo> findByIdEquipo(Equipo equipo);
    
    void deleteByIdEquipo_Id(Integer equipoId);
}