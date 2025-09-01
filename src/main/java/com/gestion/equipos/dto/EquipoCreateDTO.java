package com.gestion.equipos.dto;

import com.gestion.equipos.entity.enums.Alimentacion;
import com.gestion.equipos.entity.enums.EstadoEquipo;
import com.gestion.equipos.entity.enums.TipoEquipo;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipoCreateDTO {
    
    // Campos base de Equipo (según entidad real)
    @Size(max = 20, message = "El identificador no puede exceder 20 caracteres")
    private String identificador; // Opcional - se auto-genera si es null
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    private String nombre;
    
    @NotNull(message = "La marca es obligatoria")
    private Integer marcaId;
    
    @NotBlank(message = "El color es obligatorio")
    @Size(max = 50, message = "El color no puede exceder 50 caracteres")
    private String color;
    
    @NotNull(message = "El valor es obligatorio")
    @DecimalMin(value = "0.0", message = "El valor no puede ser negativo")
    private BigDecimal valor;
    
    @NotBlank(message = "La serie es obligatoria")
    @Size(max = 50, message = "La serie no puede exceder 50 caracteres")
    private String serie;
    
    private String extras; // text - opcional
    
    @NotNull(message = "El tipo de equipo es obligatorio")
    private TipoEquipo tipoEquipo;
    
    private Alimentacion tipoAlimentacion = Alimentacion.NINGUNA; // Default
    
    private EstadoEquipo estado = EstadoEquipo.ACTIVO; // Default ACTIVO
    
    private Integer ubicacionId; // Opcional
    
    private Integer usuarioAsignadoId; // Opcional - para asignación inicial
    
    // Campos específicos por tipo (solo UNO será requerido según tipoEquipo)
    private VehiculoCreateDTO vehiculo;
    private ElectronicoCreateDTO electronico;
    private MobiliarioCreateDTO mobiliario;
    private HerramientaCreateDTO herramienta;
    
    /**
     * Validación programática - se ejecuta en el service
     */
    public void validate() {
        if (tipoEquipo == null) {
            throw new IllegalArgumentException("Tipo de equipo es obligatorio");
        }
        
        switch (tipoEquipo) {
            case VEHICULO:
                if (vehiculo == null) {
                    throw new IllegalArgumentException("Datos de vehículo son requeridos para tipo VEHICULO");
                }
                vehiculo.validate();
                break;
                
            case ELECTRONICO:
                if (electronico == null) {
                    throw new IllegalArgumentException("Datos de electrónico son requeridos para tipo ELECTRONICO");
                }
                electronico.validate();
                break;
                
            case MOBILIARIO:
                if (mobiliario == null) {
                    throw new IllegalArgumentException("Datos de mobiliario son requeridos para tipo MOBILIARIO");
                }
                mobiliario.validate();
                break;
                
            case HERRAMIENTA:
                if (herramienta == null) {
                    throw new IllegalArgumentException("Datos de herramienta son requeridos para tipo HERRAMIENTA");
                }
                herramienta.validate();
                break;
        }
    }
}