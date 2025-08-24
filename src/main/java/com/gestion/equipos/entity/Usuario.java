package com.gestion.equipos.entity;

import com.gestion.equipos.config.PostgreSQLEnumJdbcType;
import com.gestion.equipos.entity.enums.EstadoUsuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "usuario")
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(unique = true, nullable = false, length = 20)
    private String codigo;
    
    @Column(unique = true, nullable = false, length = 50)
    private String usuario;
    
    @NotNull
    @Size(max = 50)
    @Column(name = "primer_nombre", nullable = false)
    private String primerNombre;
    
    @Size(max = 50)
    @Column(name = "segundo_nombre")
    private String segundoNombre;
    
    @NotNull
    @Size(max = 50)
    @Column(name = "primer_apellido", nullable = false)
    private String primerApellido;
    
    @Size(max = 50)
    @Column(name = "segundo_apellido")
    private String segundoApellido;
    
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "estado", columnDefinition = "estado_usuario")
    private EstadoUsuario estado = EstadoUsuario.ACTIVO;
    
    @NotNull
    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;
    
    @Column(name = "fecha_salida")
    private LocalDate fechaSalida;
    
    @NotNull
    @Column(name = "is_admin", nullable = false)
    private Boolean isAdmin = false;
    
    @NotNull
    @Size(max = 100)
    @Column(nullable = false)
    private String password;
    
    @Column(name = "fecha_commit", columnDefinition = "timestamp default now()")
    private LocalDateTime fechaCommit = LocalDateTime.now();
}