package com.gestion.equipos.service;

import com.gestion.equipos.dto.*;
import com.gestion.equipos.entity.*;
import com.gestion.equipos.entity.enums.Alimentacion;
import com.gestion.equipos.entity.enums.EstadoEquipo;
import com.gestion.equipos.entity.enums.TipoEquipo;
import com.gestion.equipos.repository.*;
import com.querydsl.core.BooleanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EquipoServiceImpl implements EquipoService {
    
    @Autowired
    private EquipoRepository equipoRepository;
    
    @Autowired
    private VehiculoRepository vehiculoRepository;
    
    @Autowired
    private ElectronicoRepository electronicoRepository;
    
    @Autowired
    private MobiliarioRepository mobiliarioRepository;
    
    @Autowired
    private HerramientaRepository herramientaRepository;
    
    @Autowired
    private MarcaRepository marcaRepository;
    
    @Autowired
    private UbicacionRepository ubicacionRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private AuthService authService;

    @Override
    @Transactional(readOnly = true)
    public Page<Equipo> findAllWithFilters(EquipoFilterDTO filters, Pageable pageable) {
        BooleanBuilder predicate = new BooleanBuilder();
        QEquipo equipo = QEquipo.equipo;
        
        if (StringUtils.hasText(filters.getIdentificador())) {
            predicate.and(equipo.identificador.containsIgnoreCase(filters.getIdentificador().trim()));
        }
        
        if (StringUtils.hasText(filters.getNombre())) {
            predicate.and(equipo.nombre.containsIgnoreCase(filters.getNombre().trim()));
        }
        
        if (StringUtils.hasText(filters.getSerie())) {
            predicate.and(equipo.serie.containsIgnoreCase(filters.getSerie().trim()));
        }
        
        if (filters.getMarcaId() != null) {
            predicate.and(equipo.idMarca.id.eq(filters.getMarcaId()));
        }
        
        if (filters.getTipoEquipo() != null) {
            predicate.and(equipo.tipoEquipo.eq(filters.getTipoEquipo()));
        }
        
        if (filters.getEstado() != null) {
            predicate.and(equipo.estado.eq(filters.getEstado()));
        }
        
        if (filters.getUbicacionId() != null) {
            predicate.and(equipo.idUbicacion.id.eq(filters.getUbicacionId()));
        }
        
        if (filters.getUsuarioAsignadoId() != null) {
            predicate.and(equipo.idUsuario.id.eq(filters.getUsuarioAsignadoId()));
        }
        
        if (filters.getAsignado() != null) {
            if (filters.getAsignado()) {
                predicate.and(equipo.idUsuario.isNotNull());
            } else {
                predicate.and(equipo.idUsuario.isNull());
            }
        }
        
        return equipoRepository.findAll(predicate, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EquipoDTO> findAllEquipoDTOWithFilters(EquipoFilterDTO filters, Pageable pageable) {
        Page<Equipo> equipoPage = findAllWithFilters(filters, pageable);
        
        List<EquipoDTO> equipoDTOs = equipoPage.getContent().stream()
                .map(this::convertToDTO)
                .toList();
        
        return new PageImpl<>(equipoDTOs, pageable, equipoPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Equipo> findAllActive() {
        return equipoRepository.findByEstadoOrderByIdentificadorAsc(EstadoEquipo.ACTIVO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Equipo> findById(Integer id) {
        return equipoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EquipoDTO> findEquipoDTOById(Integer id) {
        return equipoRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional
    public EquipoDTO create(EquipoCreateDTO createDTO) {
        createDTO.validate();
        
        Equipo equipo = new Equipo();
        // Si identificador está vacío, usar null para que el trigger genere automáticamente
        String identificador = createDTO.getIdentificador();
        equipo.setIdentificador((identificador != null && !identificador.trim().isEmpty()) ? identificador.trim() : null);
        equipo.setNombre(createDTO.getNombre().trim());
        equipo.setIdMarca(marcaRepository.findById(createDTO.getMarcaId())
                .orElseThrow(() -> new IllegalArgumentException("Marca no encontrada")));
        equipo.setColor(createDTO.getColor().trim());
        equipo.setValor(createDTO.getValor());
        equipo.setSerie(createDTO.getSerie().trim());
        equipo.setExtras(createDTO.getExtras());
        equipo.setTipoEquipo(createDTO.getTipoEquipo());
        equipo.setTipoAlimentacion(createDTO.getTipoAlimentacion() != null ? createDTO.getTipoAlimentacion() : Alimentacion.NINGUNA);
        equipo.setEstado(createDTO.getEstado() != null ? createDTO.getEstado() : EstadoEquipo.ACTIVO);
        equipo.setCreadoPor(authService.getCurrentUser());
        equipo.setFechaCommit(LocalDateTime.now());
        
        if (createDTO.getUbicacionId() != null) {
            equipo.setIdUbicacion(ubicacionRepository.findById(createDTO.getUbicacionId())
                    .orElseThrow(() -> new IllegalArgumentException("Ubicación no encontrada")));
        }
        
        if (createDTO.getUsuarioAsignadoId() != null) {
            equipo.setIdUsuario(usuarioRepository.findById(createDTO.getUsuarioAsignadoId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado")));
        }
        
        equipo = equipoRepository.save(equipo);
        
        createTipoEspecifico(equipo, createDTO);
        
        return convertToDTO(equipo);
    }

    @Override
    @Transactional
    public EquipoDTO update(Integer id, EquipoUpdateDTO updateDTO) {
        updateDTO.validate();
        
        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado"));
        
        if (!equipo.getTipoEquipo().equals(updateDTO.getTipoEquipo())) {
            throw new IllegalArgumentException("No se puede cambiar el tipo de equipo");
        }
        
        equipo.setNombre(updateDTO.getNombre().trim());
        equipo.setIdMarca(marcaRepository.findById(updateDTO.getMarcaId())
                .orElseThrow(() -> new IllegalArgumentException("Marca no encontrada")));
        equipo.setColor(updateDTO.getColor().trim());
        equipo.setValor(updateDTO.getValor());
        equipo.setSerie(updateDTO.getSerie().trim());
        equipo.setExtras(updateDTO.getExtras());
        equipo.setTipoAlimentacion(updateDTO.getTipoAlimentacion());
        equipo.setEstado(updateDTO.getEstado());
        equipo.setFechaCommit(LocalDateTime.now());
        
        if (updateDTO.getUbicacionId() != null) {
            equipo.setIdUbicacion(ubicacionRepository.findById(updateDTO.getUbicacionId())
                    .orElseThrow(() -> new IllegalArgumentException("Ubicación no encontrada")));
        } else {
            equipo.setIdUbicacion(null);
        }
        
        equipo = equipoRepository.save(equipo);
        
        updateTipoEspecifico(equipo, updateDTO);
        
        return convertToDTO(equipo);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado"));
        
        equipo.setEstado(EstadoEquipo.SUSPENDIDO);
        equipo.setFechaCommit(LocalDateTime.now());
        equipoRepository.save(equipo);
    }

    @Override
    @Transactional
    public EquipoDTO cambiarEstado(Integer id, EstadoEquipo nuevoEstado, String motivo) {
        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado"));
        
        EstadoEquipo estadoAnterior = equipo.getEstado();
        
        if (estadoAnterior == EstadoEquipo.SUSPENDIDO) {
            throw new IllegalStateException("No se puede cambiar el estado de un equipo SUSPENDIDO");
        }
        
        equipo.setEstado(nuevoEstado);
        equipo.setFechaCommit(LocalDateTime.now());
        equipo = equipoRepository.save(equipo);
        
        return convertToDTO(equipo);
    }

    @Override
    @Transactional(readOnly = true)
    public EquipoDTO convertToDTO(Equipo equipo) {
        EquipoDTO dto = new EquipoDTO();
        dto.setId(equipo.getId());
        dto.setIdentificador(equipo.getIdentificador());
        dto.setNombre(equipo.getNombre());
        dto.setMarcaId(equipo.getIdMarca().getId());
        dto.setMarcaNombre(equipo.getIdMarca().getNombre());
        dto.setColor(equipo.getColor());
        dto.setValor(equipo.getValor());
        dto.setSerie(equipo.getSerie());
        dto.setExtras(equipo.getExtras());
        dto.setTipoEquipo(equipo.getTipoEquipo());
        dto.setTipoAlimentacion(equipo.getTipoAlimentacion());
        dto.setEstado(equipo.getEstado());
        dto.setFechaCommit(equipo.getFechaCommit());
        
        if (equipo.getIdUbicacion() != null) {
            dto.setUbicacionId(equipo.getIdUbicacion().getId());
            dto.setUbicacionNombre(equipo.getIdUbicacion().getNombre());
        }
        
        if (equipo.getIdUsuario() != null) {
            dto.setUsuarioAsignadoId(equipo.getIdUsuario().getId());
            dto.setUsuarioAsignadoNombre(equipo.getIdUsuario().getPrimerNombre() + " " + 
                    equipo.getIdUsuario().getPrimerApellido());
        }
        
        if (equipo.getCreadoPor() != null) {
            dto.setCreadoPor(equipo.getCreadoPor().getPrimerNombre() + " " + 
                    equipo.getCreadoPor().getPrimerApellido());
        }
        
        loadTipoEspecifico(equipo, dto);
        
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipoDTO> findByTipoEquipo(TipoEquipo tipo) {
        return equipoRepository.findByTipoEquipoOrderByIdentificadorAsc(tipo).stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipoDTO> findEquiposByUsuario(Integer usuarioId) {
        return equipoRepository.findByIdUsuario_IdOrderByIdentificadorAsc(usuarioId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipoDTO> findEquiposLibres() {
        return equipoRepository.findByIdUsuarioIsNullAndEstadoOrderByIdentificadorAsc(EstadoEquipo.ACTIVO).stream()
                .map(this::convertToDTO)
                .toList();
    }

    private void createTipoEspecifico(Equipo equipo, EquipoCreateDTO createDTO) {
        switch (createDTO.getTipoEquipo()) {
            case VEHICULO -> {
                Vehiculo vehiculo = new Vehiculo();
                vehiculo.setNoMotor(createDTO.getVehiculo().getNoMotor());
                vehiculo.setVin(createDTO.getVehiculo().getVin());
                vehiculo.setCilindrada(createDTO.getVehiculo().getCilindrada());
                vehiculo.setPlaca(createDTO.getVehiculo().getPlaca());
                vehiculo.setModelo(createDTO.getVehiculo().getModelo());
                vehiculo.setIdEquipo(equipo);
                vehiculoRepository.save(vehiculo);
            }
            case ELECTRONICO -> {
                Electronico electronico = new Electronico();
                electronico.setImei(createDTO.getElectronico().getImei());
                electronico.setSistemaOperativo(createDTO.getElectronico().getSistemaOperativo());
                electronico.setConectividad(createDTO.getElectronico().getConectividad());
                electronico.setOperador(createDTO.getElectronico().getOperador());
                electronico.setIdEquipo(equipo);
                electronicoRepository.save(electronico);
            }
            case MOBILIARIO -> {
                Mobiliario mobiliario = new Mobiliario();
                mobiliario.setMaterial(createDTO.getMobiliario().getMaterial());
                mobiliario.setAltura(createDTO.getMobiliario().getAltura());
                mobiliario.setAncho(createDTO.getMobiliario().getAncho());
                mobiliario.setProfundidad(createDTO.getMobiliario().getProfundidad());
                mobiliario.setCantidadPiezas(createDTO.getMobiliario().getCantidadPiezas());
                mobiliario.setIdEquipo(equipo);
                mobiliarioRepository.save(mobiliario);
            }
            case HERRAMIENTA -> {
                Herramienta herramienta = new Herramienta();
                herramienta.setMaterial(createDTO.getHerramienta().getMaterial());
                herramienta.setIdEquipo(equipo);
                herramientaRepository.save(herramienta);
            }
        }
    }

    private void updateTipoEspecifico(Equipo equipo, EquipoUpdateDTO updateDTO) {
        switch (equipo.getTipoEquipo()) {
            case VEHICULO -> {
                Vehiculo vehiculo = vehiculoRepository.findByIdEquipo(equipo)
                        .orElseThrow(() -> new IllegalStateException("Datos de vehículo no encontrados"));
                vehiculo.setNoMotor(updateDTO.getVehiculo().getNoMotor());
                vehiculo.setVin(updateDTO.getVehiculo().getVin());
                vehiculo.setCilindrada(updateDTO.getVehiculo().getCilindrada());
                vehiculo.setPlaca(updateDTO.getVehiculo().getPlaca());
                vehiculo.setModelo(updateDTO.getVehiculo().getModelo());
                vehiculoRepository.save(vehiculo);
            }
            case ELECTRONICO -> {
                Electronico electronico = electronicoRepository.findByIdEquipo(equipo)
                        .orElseThrow(() -> new IllegalStateException("Datos de electrónico no encontrados"));
                electronico.setImei(updateDTO.getElectronico().getImei());
                electronico.setSistemaOperativo(updateDTO.getElectronico().getSistemaOperativo());
                electronico.setConectividad(updateDTO.getElectronico().getConectividad());
                electronico.setOperador(updateDTO.getElectronico().getOperador());
                electronicoRepository.save(electronico);
            }
            case MOBILIARIO -> {
                Mobiliario mobiliario = mobiliarioRepository.findByIdEquipo(equipo)
                        .orElseThrow(() -> new IllegalStateException("Datos de mobiliario no encontrados"));
                mobiliario.setMaterial(updateDTO.getMobiliario().getMaterial());
                mobiliario.setAltura(updateDTO.getMobiliario().getAltura());
                mobiliario.setAncho(updateDTO.getMobiliario().getAncho());
                mobiliario.setProfundidad(updateDTO.getMobiliario().getProfundidad());
                mobiliario.setCantidadPiezas(updateDTO.getMobiliario().getCantidadPiezas());
                mobiliarioRepository.save(mobiliario);
            }
            case HERRAMIENTA -> {
                Herramienta herramienta = herramientaRepository.findByIdEquipo(equipo)
                        .orElseThrow(() -> new IllegalStateException("Datos de herramienta no encontrados"));
                herramienta.setMaterial(updateDTO.getHerramienta().getMaterial());
                herramientaRepository.save(herramienta);
            }
        }
    }

    private void loadTipoEspecifico(Equipo equipo, EquipoDTO dto) {
        switch (equipo.getTipoEquipo()) {
            case VEHICULO -> {
                vehiculoRepository.findByIdEquipo(equipo).ifPresent(vehiculo -> {
                    VehiculoDTO vehiculoDTO = new VehiculoDTO();
                    vehiculoDTO.setId(vehiculo.getId());
                    vehiculoDTO.setNoMotor(vehiculo.getNoMotor());
                    vehiculoDTO.setVin(vehiculo.getVin());
                    vehiculoDTO.setCilindrada(vehiculo.getCilindrada());
                    vehiculoDTO.setPlaca(vehiculo.getPlaca());
                    vehiculoDTO.setModelo(vehiculo.getModelo());
                    dto.setVehiculo(vehiculoDTO);
                });
            }
            case ELECTRONICO -> {
                electronicoRepository.findByIdEquipo(equipo).ifPresent(electronico -> {
                    ElectronicoDTO electronicoDTO = new ElectronicoDTO();
                    electronicoDTO.setId(electronico.getId());
                    electronicoDTO.setImei(electronico.getImei());
                    electronicoDTO.setSistemaOperativo(electronico.getSistemaOperativo());
                    electronicoDTO.setConectividad(electronico.getConectividad());
                    electronicoDTO.setOperador(electronico.getOperador());
                    dto.setElectronico(electronicoDTO);
                });
            }
            case MOBILIARIO -> {
                mobiliarioRepository.findByIdEquipo(equipo).ifPresent(mobiliario -> {
                    MobiliarioDTO mobiliarioDTO = new MobiliarioDTO();
                    mobiliarioDTO.setId(mobiliario.getId());
                    mobiliarioDTO.setMaterial(mobiliario.getMaterial());
                    mobiliarioDTO.setAltura(mobiliario.getAltura());
                    mobiliarioDTO.setAncho(mobiliario.getAncho());
                    mobiliarioDTO.setProfundidad(mobiliario.getProfundidad());
                    mobiliarioDTO.setCantidadPiezas(mobiliario.getCantidadPiezas());
                    dto.setMobiliario(mobiliarioDTO);
                });
            }
            case HERRAMIENTA -> {
                herramientaRepository.findByIdEquipo(equipo).ifPresent(herramienta -> {
                    HerramientaDTO herramientaDTO = new HerramientaDTO();
                    herramientaDTO.setId(herramienta.getId());
                    herramientaDTO.setMaterial(herramienta.getMaterial());
                    dto.setHerramienta(herramientaDTO);
                });
            }
        }
    }
}