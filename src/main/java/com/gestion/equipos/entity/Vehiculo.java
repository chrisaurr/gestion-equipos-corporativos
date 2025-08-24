package com.gestion.equipos.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "vehiculo")
public class Vehiculo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotNull
    @Size(max = 20)
    @Column(name = "no_motor", nullable = false)
    private String noMotor;
    
    @NotNull
    @Size(max = 20)
    @Column(nullable = false, unique = true)
    private String vin;
    
    @NotNull
    @Column(nullable = false)
    private Integer cilindrada;
    
    @NotNull
    @Size(max = 10)
    @Column(nullable = false, unique = true)
    private String placa;
    
    @NotNull
    @Column(nullable = false)
    private Integer modelo;
    
    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_equipo", nullable = false, unique = true)
    private Equipo idEquipo;
}