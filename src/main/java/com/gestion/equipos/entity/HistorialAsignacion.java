package com.gestion.equipos.entity;

import com.gestion.equipos.config.PostgreSQLEnumJdbcType;
import com.gestion.equipos.entity.enums.EstadoDevolucion;
import com.gestion.equipos.entity.enums.MotivoCambio;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;

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
    @JoinColumn(name = "id_usuario_anterior")
    private Usuario idUsuarioAnterior;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_nuevo")
    private Usuario idUsuarioNuevo;
    
    @NotNull
    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDateTime fechaAsignacion;
    
    @Column(name = "fecha_devolucion")
    private LocalDateTime fechaDevolucion;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "motivo_cambio", nullable = false, columnDefinition = "motivo_cambio")
    private MotivoCambio motivoCambio;
    
    @Column(columnDefinition = "text")
    private String observaciones;
    
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "estado_devolucion", columnDefinition = "estado_devolucion")
    private EstadoDevolucion estadoDevolucion = EstadoDevolucion.PENDIENTE;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignado_por", nullable = false)
    private Usuario asignadoPor;
    
    @Column(name = "fecha_commit", columnDefinition = "timestamp default now()")
    private LocalDateTime fechaCommit = LocalDateTime.now();
}