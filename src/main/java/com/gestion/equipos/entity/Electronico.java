package com.gestion.equipos.entity;

import com.gestion.equipos.config.PostgreSQLEnumJdbcType;
import com.gestion.equipos.entity.enums.Conectividad;
import com.gestion.equipos.entity.enums.Operador;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "electronico")
public class Electronico {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Size(max = 20)
    @Column(unique = true)
    private String imei;
    
    @Size(max = 20)
    @Column(name = "sistema_operativo")
    private String sistemaOperativo;
    
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "conectividad", columnDefinition = "conectividad")
    private Conectividad conectividad = Conectividad.NINGUNO;
    
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "operador", columnDefinition = "operador")
    private Operador operador = Operador.NINGUNO;
    
    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_equipo", nullable = false, unique = true)
    private Equipo idEquipo;
}