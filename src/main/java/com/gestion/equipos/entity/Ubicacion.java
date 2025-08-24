package com.gestion.equipos.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ubicacion")
public class Ubicacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotNull
    @Size(max = 100)
    @Column(nullable = false)
    private String nombre;
    
    @Size(max = 200)
    private String descripcion;
    
    @Column(columnDefinition = "boolean default true")
    private Boolean activo = true;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por", nullable = false)
    private Usuario creadoPor;
    
    @Column(name = "fecha_commit", columnDefinition = "timestamp default now()")
    private LocalDateTime fechaCommit = LocalDateTime.now();
}