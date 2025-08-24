package com.gestion.equipos.entity;

import com.gestion.equipos.entity.enums.MotivoCambio;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "historial_asignacion")
public class HistorialAsignacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_equipo", nullable = false)
    private Equipo idEquipo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado_anterior")
    private Empleado idEmpleadoAnterior;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado_nuevo")
    private Empleado idEmpleadoNuevo;
    
    @NotNull
    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDateTime fechaAsignacion;
    
    @Column(name = "fecha_devolucion")
    private LocalDateTime fechaDevolucion;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "motivo_cambio", nullable = false)
    private MotivoCambio motivoCambio;
    
    @Column(columnDefinition = "text")
    private String observaciones;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignado_por", nullable = false)
    private Usuario asignadoPor;
    
    @Column(name = "fecha_commit", columnDefinition = "timestamp default now()")
    private LocalDateTime fechaCommit = LocalDateTime.now();
}