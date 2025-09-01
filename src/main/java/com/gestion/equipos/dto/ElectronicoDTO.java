package com.gestion.equipos.dto;

import com.gestion.equipos.entity.enums.Conectividad;
import com.gestion.equipos.entity.enums.Operador;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElectronicoDTO {
    
    private Integer id;
    private String imei;
    private String sistemaOperativo;
    private Conectividad conectividad;
    private Operador operador;
}