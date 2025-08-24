package com.gestion.equipos.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mobiliario")
public class Mobiliario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotNull
    @Size(max = 50)
    @Column(nullable = false)
    private String material;
    
    @NotNull
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal altura;
    
    @NotNull
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal ancho;
    
    @NotNull
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal profundidad;
    
    @Column(name = "cantidad_piezas", columnDefinition = "integer default 1")
    private Integer cantidadPiezas = 1;
    
    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_equipo", nullable = false, unique = true)
    private Equipo idEquipo;
}