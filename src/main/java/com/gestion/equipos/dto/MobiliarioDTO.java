package com.gestion.equipos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MobiliarioDTO {
    
    private Integer id;
    private String material;
    private BigDecimal altura;
    private BigDecimal ancho;
    private BigDecimal profundidad;
    private Integer cantidadPiezas;
}