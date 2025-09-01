package com.gestion.equipos.repository;

import com.gestion.equipos.entity.Electronico;
import com.gestion.equipos.entity.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ElectronicoRepository extends JpaRepository<Electronico, Integer>, QuerydslPredicateExecutor<Electronico> {
    
    Optional<Electronico> findByIdEquipo_Id(Integer equipoId);
    
    Optional<Electronico> findByIdEquipo(Equipo equipo);
    
    void deleteByIdEquipo_Id(Integer equipoId);
}