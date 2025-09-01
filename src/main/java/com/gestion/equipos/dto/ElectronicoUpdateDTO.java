package com.gestion.equipos.dto;

import com.gestion.equipos.entity.enums.Conectividad;
import com.gestion.equipos.entity.enums.Operador;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElectronicoUpdateDTO {
    
    @Size(max = 20, message = "El IMEI no puede exceder 20 caracteres")
    private String imei; // Opcional y único
    
    @Size(max = 20, message = "El sistema operativo no puede exceder 20 caracteres")
    private String sistemaOperativo;
    
    private Conectividad conectividad;
    
    private Operador operador;
    
    public void validate() {
        // Validaciones específicas si es necesario
        // Para electrónicos no hay campos obligatorios según la entidad
    }
}