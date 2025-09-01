package com.gestion.equipos.repository;

import com.gestion.equipos.entity.Reporte;
import com.gestion.equipos.entity.enums.EstadoReporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Integer>, QuerydslPredicateExecutor<Reporte> {
    
    List<Reporte> findByActivoTrueOrderByFechaCommitDesc();
    
    @Query("SELECT r FROM Reporte r WHERE r.idEquipo.id = :equipoId AND r.estado IN (:abierto, :enProceso) AND r.activo = true")
    Optional<Reporte> findActiveReportByEquipo(@Param("equipoId") Integer equipoId, 
                                              @Param("abierto") EstadoReporte abierto,
                                              @Param("enProceso") EstadoReporte enProceso);
    
    @Query("SELECT r FROM Reporte r WHERE r.idEquipo.id = :equipoId AND r.activo = true ORDER BY r.fechaCommit DESC")
    List<Reporte> findByEquipoIdAndActivoTrueOrderByFechaCommitDesc(@Param("equipoId") Integer equipoId);
    
    @Query("SELECT r FROM Reporte r WHERE r.idUsuario.id = :usuarioId AND r.activo = true ORDER BY r.fechaCommit DESC")
    List<Reporte> findByUsuarioIdAndActivoTrueOrderByFechaCommitDesc(@Param("usuarioId") Integer usuarioId);
    
    @Query("SELECT r FROM Reporte r WHERE r.estado = :estado AND r.activo = true ORDER BY r.fechaCommit DESC")
    List<Reporte> findByEstadoAndActivoTrueOrderByFechaCommitDesc(@Param("estado") EstadoReporte estado);
}