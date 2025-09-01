package com.gestion.equipos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoDTO {
    
    private Integer id;
    private String noMotor;
    private String vin;
    private Integer cilindrada;
    private String placa;
    private Integer modelo;
}