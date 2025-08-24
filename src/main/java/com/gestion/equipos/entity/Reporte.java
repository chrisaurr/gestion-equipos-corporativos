package com.gestion.equipos.entity;

import com.gestion.equipos.entity.enums.EstadoReporte;
import com.gestion.equipos.entity.enums.PrioridadReporte;
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
@Table(name = "reporte")
public class Reporte {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(columnDefinition = "text")
    private String observacion;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_causa", nullable = false)
    private Causa idCausa;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_equipo", nullable = false)
    private Equipo idEquipo;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadReporte prioridad;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado idEmpleado;
    
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(20) default 'ABIERTO'")
    private EstadoReporte estado = EstadoReporte.ABIERTO;
    
    @Column(name = "fecha_commit", columnDefinition = "timestamp default now()")
    private LocalDateTime fechaCommit = LocalDateTime.now();
}