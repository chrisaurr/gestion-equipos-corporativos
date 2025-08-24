package com.gestion.equipos.service;

import com.gestion.equipos.entity.Empleado;
import com.gestion.equipos.entity.Usuario;
import com.gestion.equipos.repository.EmpleadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmpleadoServiceImpl implements EmpleadoService {

    private final EmpleadoRepository empleadoRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Empleado> findByUsuario(Usuario usuario) {
        return empleadoRepository.findByIdUsuario(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Empleado> findByUsuarioId(Integer usuarioId) {
        return empleadoRepository.findByIdUsuario_Id(usuarioId);
    }

    @Override
    public Empleado save(Empleado empleado) {
        return empleadoRepository.save(empleado);
    }

    @Override
    public void deleteByUsuario(Usuario usuario) {
        empleadoRepository.deleteByIdUsuario(usuario);
    }

    @Override
    public void deleteByUsuarioId(Integer usuarioId) {
        empleadoRepository.deleteByIdUsuario_Id(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsuario(Usuario usuario) {
        return empleadoRepository.existsByIdUsuario(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsuarioId(Integer usuarioId) {
        return empleadoRepository.existsByIdUsuario_Id(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByAreaAndUsuarioActivo(Integer areaId) {
        return empleadoRepository.existsByIdArea_IdAndIdUsuario_Estado(areaId, 
                com.gestion.equipos.entity.enums.EstadoUsuario.ACTIVO);
    }
}