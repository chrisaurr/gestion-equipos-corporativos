package com.gestion.equipos.entity;

import com.gestion.equipos.entity.enums.Alimentacion;
import com.gestion.equipos.entity.enums.EstadoEquipo;
import com.gestion.equipos.entity.enums.TipoEquipo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "equipo")
public class Equipo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(length = 20)
    private String identificador;
    
    @NotNull
    @Size(max = 50)
    @Column(nullable = false)
    private String nombre;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_marca", nullable = false)
    private Marca idMarca;
    
    @NotNull
    @Size(max = 50)
    @Column(nullable = false)
    private String color;
    
    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;
    
    @NotNull
    @Size(max = 50)
    @Column(nullable = false)
    private String serie;
    
    @Column(columnDefinition = "text")
    private String extras;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_equipo", nullable = false)
    private TipoEquipo tipoEquipo;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_alimentacion", columnDefinition = "varchar(20) default 'NINGUNA'")
    private Alimentacion tipoAlimentacion = Alimentacion.NINGUNA;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado")
    private Empleado idEmpleado;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ubicacion")
    private Ubicacion idUbicacion;
    
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(20) default 'ACTIVO'")
    private EstadoEquipo estado = EstadoEquipo.ACTIVO;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por", nullable = false)
    private Usuario creadoPor;
    
    @Column(name = "fecha_commit", columnDefinition = "timestamp default now()")
    private LocalDateTime fechaCommit = LocalDateTime.now();
}