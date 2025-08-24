package com.gestion.equipos.config;

import com.gestion.equipos.dto.UsuarioEmpleadoCreateDTO;
import com.gestion.equipos.dto.UsuarioEmpleadoDTO;
import com.gestion.equipos.dto.UsuarioEmpleadoUpdateDTO;
import com.gestion.equipos.entity.Usuario;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        configureUsuarioEmpleadoMappings(mapper);
        
        return mapper;
    }

    private void configureUsuarioEmpleadoMappings(ModelMapper mapper) {
        
        mapper.createTypeMap(UsuarioEmpleadoCreateDTO.class, Usuario.class)
                .addMappings(mapping -> {
                    mapping.skip(Usuario::setId);
                    mapping.skip(Usuario::setFechaCommit);
                });

        mapper.createTypeMap(UsuarioEmpleadoUpdateDTO.class, Usuario.class)
                .addMappings(mapping -> {
                    mapping.skip(Usuario::setId);
                    mapping.skip(Usuario::setFechaCommit);
                    mapping.when(ctx -> !((UsuarioEmpleadoUpdateDTO) ctx.getSource()).isChangePassword())
                            .skip(UsuarioEmpleadoUpdateDTO::getPassword, Usuario::setPassword);
                });

        mapper.createTypeMap(Usuario.class, UsuarioEmpleadoDTO.class)
                .addMappings(mapping -> {
                    mapping.map(Usuario::getId, UsuarioEmpleadoDTO::setUsuarioId);
                });

        mapper.createTypeMap(UsuarioEmpleadoDTO.class, UsuarioEmpleadoUpdateDTO.class);
    }
}