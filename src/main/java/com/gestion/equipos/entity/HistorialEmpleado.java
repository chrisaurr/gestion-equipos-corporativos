package com.gestion.equipos.entity;

import com.gestion.equipos.config.PostgreSQLEnumJdbcType;
import com.gestion.equipos.entity.enums.TipoCambioEmpleado;
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
@Table(name = "historial_empleado")
public class HistorialEmpleado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_area_anterior")
    private Area areaAnterior;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_area_nueva")
    private Area areaNueva;
    
    @Column(name = "cargo_anterior")
    private String cargoAnterior;
    
    @Column(name = "cargo_nuevo")
    private String cargoNuevo;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "tipo_cambio", nullable = false, columnDefinition = "tipo_cambio_empleado")
    private TipoCambioEmpleado tipoCambio;
    
    @Column(columnDefinition = "text")
    private String motivo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por")
    private Usuario creadoPor;
    
    @NotNull
    @Column(name = "fecha_cambio", nullable = false, columnDefinition = "timestamp default now()")
    private LocalDateTime fechaCambio = LocalDateTime.now();
}