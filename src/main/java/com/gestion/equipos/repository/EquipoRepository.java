package com.gestion.equipos.repository;

import com.gestion.equipos.entity.Equipo;
import com.gestion.equipos.entity.enums.EstadoEquipo;
import com.gestion.equipos.entity.enums.TipoEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Integer>, QuerydslPredicateExecutor<Equipo> {
    
    List<Equipo> findByEstadoOrderByIdentificadorAsc(EstadoEquipo estado);
    
    List<Equipo> findByTipoEquipoOrderByIdentificadorAsc(TipoEquipo tipoEquipo);
    
    List<Equipo> findByIdUsuario_IdOrderByIdentificadorAsc(Integer usuarioId);
    
    List<Equipo> findByIdUsuarioIsNullAndEstadoOrderByIdentificadorAsc(EstadoEquipo estado);
}