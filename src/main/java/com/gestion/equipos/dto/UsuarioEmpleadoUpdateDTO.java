package com.gestion.equipos.dto;

import com.gestion.equipos.entity.enums.EstadoUsuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioEmpleadoUpdateDTO {
    
    @NotBlank(message = "El código es obligatorio")
    @Size(max = 20, message = "El código no puede tener más de 20 caracteres")
    private String codigo;
    
    @NotBlank(message = "El usuario es obligatorio")
    @Size(max = 50, message = "El usuario no puede tener más de 50 caracteres")
    private String usuario;
    
    @NotBlank(message = "El primer nombre es obligatorio")
    @Size(max = 50, message = "El primer nombre no puede tener más de 50 caracteres")
    private String primerNombre;
    
    @Size(max = 50, message = "El segundo nombre no puede tener más de 50 caracteres")
    private String segundoNombre;
    
    @NotBlank(message = "El primer apellido es obligatorio")
    @Size(max = 50, message = "El primer apellido no puede tener más de 50 caracteres")
    private String primerApellido;
    
    @Size(max = 50, message = "El segundo apellido no puede tener más de 50 caracteres")
    private String segundoApellido;
    
    @NotNull(message = "El estado es obligatorio")
    private EstadoUsuario estado;
    
    @NotNull(message = "La fecha de ingreso es obligatoria")
    private LocalDate fechaIngreso;
    
    private LocalDate fechaSalida;
    
    @NotNull(message = "Debe especificar si es administrador")
    private Boolean isAdmin;
    
    private String password;
    
    private String confirmPassword;
    
    private boolean changePassword = false;
    
    private Boolean esEmpleado = false;
    
    private Integer empleadoId;
    
    private Integer areaId;
    
    @Size(max = 100, message = "El cargo no puede tener más de 100 caracteres")
    private String cargo;
    
    private String observaciones;
}