package com.gestion.equipos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoCreateDTO {
    
    @NotBlank(message = "El número de motor es obligatorio")
    @Size(max = 20, message = "El número de motor no puede exceder 20 caracteres")
    private String noMotor;
    
    @NotBlank(message = "El VIN es obligatorio")
    @Size(max = 20, message = "El VIN no puede exceder 20 caracteres")
    private String vin;
    
    @NotNull(message = "La cilindrada es obligatoria")
    @Min(value = 1, message = "La cilindrada debe ser mayor a 0")
    private Integer cilindrada;
    
    @NotBlank(message = "La placa es obligatoria")
    @Size(max = 10, message = "La placa no puede exceder 10 caracteres")
    private String placa;
    
    @NotNull(message = "El modelo es obligatorio")
    @Min(value = 1900, message = "El modelo debe ser mayor a 1900")
    private Integer modelo;
    
    public void validate() {
        if (placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException("La placa es obligatoria para vehículos");
        }
        
        if (vin == null || vin.trim().isEmpty()) {
            throw new IllegalArgumentException("El VIN es obligatorio para vehículos");
        }
        
        if (noMotor == null || noMotor.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de motor es obligatorio para vehículos");
        }
    }
}