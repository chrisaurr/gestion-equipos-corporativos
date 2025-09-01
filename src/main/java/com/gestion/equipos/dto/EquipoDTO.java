package com.gestion.equipos.dto;

import com.gestion.equipos.entity.enums.Alimentacion;
import com.gestion.equipos.entity.enums.EstadoEquipo;
import com.gestion.equipos.entity.enums.TipoEquipo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipoDTO {
    
    // Campos base del equipo (según entidad real)
    private Integer id;
    private String identificador;
    private String nombre;
    private String marcaNombre;
    private Integer marcaId;
    private String color;
    private BigDecimal valor;
    private String serie;
    private String extras;
    private TipoEquipo tipoEquipo;
    private Alimentacion tipoAlimentacion;
    private EstadoEquipo estado;
    private LocalDateTime fechaCommit;
    
    // Información de relaciones
    private String ubicacionNombre;
    private Integer ubicacionId;
    private String usuarioAsignadoNombre;
    private Integer usuarioAsignadoId;
    
    // Información de auditoría
    private String creadoPor;
    
    // Datos específicos por tipo (solo UNO será != null)
    private VehiculoDTO vehiculo;
    private ElectronicoDTO electronico;
    private MobiliarioDTO mobiliario;
    private HerramientaDTO herramienta;
    
    /**
     * Método utilitario para obtener los datos específicos del tipo
     */
    public Object getDatosEspecificos() {
        return switch (tipoEquipo) {
            case VEHICULO -> vehiculo;
            case ELECTRONICO -> electronico;
            case MOBILIARIO -> mobiliario;
            case HERRAMIENTA -> herramienta;
        };
    }
    
    /**
     * Método utilitario para verificar si tiene asignación
     */
    public boolean estaAsignado() {
        return usuarioAsignadoId != null;
    }
    
    /**
     * Método utilitario para verificar si puede asignarse
     */
    public boolean puedeAsignarse() {
        return estado == EstadoEquipo.ACTIVO && !estaAsignado();
    }
}