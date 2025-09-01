package com.gestion.equipos.repository;

import com.gestion.equipos.entity.HistorialAsignacion;
import com.gestion.equipos.entity.enums.EstadoDevolucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistorialAsignacionRepository extends JpaRepository<HistorialAsignacion, Integer>, QuerydslPredicateExecutor<HistorialAsignacion> {
    
    List<HistorialAsignacion> findByIdEquipo_IdOrderByFechaAsignacionDesc(Integer equipoId);
    
    @Query("SELECT h FROM HistorialAsignacion h " +
           "LEFT JOIN FETCH h.idUsuarioAnterior ua " +
           "LEFT JOIN FETCH h.idUsuarioNuevo un " +
           "LEFT JOIN FETCH h.asignadoPor ap " +
           "WHERE h.idEquipo.id = :equipoId " +
           "ORDER BY h.fechaAsignacion DESC")
    List<HistorialAsignacion> findHistorialWithUsersDataByEquipoId(@Param("equipoId") Integer equipoId);
    
    List<HistorialAsignacion> findByIdUsuarioNuevo_IdOrderByFechaAsignacionDesc(Integer usuarioId);
    
    @Query("SELECT h FROM HistorialAsignacion h WHERE h.idEquipo.id = :equipoId AND h.fechaDevolucion IS NULL")
    Optional<HistorialAsignacion> findActiveAssignmentByEquipo(@Param("equipoId") Integer equipoId);
    
    boolean existsByIdEquipo_Id(Integer equipoId);
    
    @Modifying
    @Query("UPDATE HistorialAsignacion h SET h.estadoDevolucion = :estado WHERE h.id = :historialId")
    void updateEstadoDevolucion(@Param("historialId") Integer historialId, @Param("estado") EstadoDevolucion estado);
    
    Optional<HistorialAsignacion> findByIdEquipo_IdAndEstadoDevolucion(Integer equipoId, EstadoDevolucion estadoDevolucion);
}