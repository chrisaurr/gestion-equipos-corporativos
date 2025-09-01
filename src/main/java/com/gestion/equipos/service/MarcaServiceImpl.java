package com.gestion.equipos.service;

import com.gestion.equipos.dto.MarcaCreateDTO;
import com.gestion.equipos.dto.MarcaDTO;
import com.gestion.equipos.dto.MarcaFilterDTO;
import com.gestion.equipos.dto.MarcaUpdateDTO;
import com.gestion.equipos.entity.Marca;
import com.gestion.equipos.entity.QMarca;
import com.gestion.equipos.entity.Usuario;
import com.gestion.equipos.repository.MarcaRepository;
import com.querydsl.core.BooleanBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MarcaServiceImpl implements MarcaService {
    
    @Autowired
    private MarcaRepository marcaRepository;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @Override
    @Transactional(readOnly = true)
    public Page<Marca> findAllWithFilters(MarcaFilterDTO filters, Pageable pageable) {
        BooleanBuilder predicate = new BooleanBuilder();
        QMarca marca = QMarca.marca;
        
        predicate.and(marca.activo.isTrue());
        
        if (StringUtils.hasText(filters.getNombre())) {
            predicate.and(marca.nombre.containsIgnoreCase(filters.getNombre().trim()));
        }
        
        // Hacer eager fetch de la relación creadoPor
        return marcaRepository.findAll(predicate, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<MarcaDTO> findAllMarcaDTOWithFilters(MarcaFilterDTO filters, Pageable pageable) {
        Page<Marca> marcaPage = findAllWithFilters(filters, pageable);
        
        List<MarcaDTO> marcaDTOs = marcaPage.getContent().stream()
                .map(this::convertToDTO)
                .toList();
        
        return new org.springframework.data.domain.PageImpl<>(marcaDTOs, pageable, marcaPage.getTotalElements());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Marca> findAllActive() {
        return marcaRepository.findByActivoTrueOrderByNombreAsc();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Marca> findById(Integer id) {
        return marcaRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<MarcaDTO> findMarcaDTOById(Integer id) {
        return marcaRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional
    public MarcaDTO create(MarcaCreateDTO createDTO) {
        String nombreTrimmed = createDTO.getNombre().trim();
        
        Optional<Marca> marcaInactiva = marcaRepository.findByNombreIgnoreCaseAndActivoFalse(nombreTrimmed);
        if (marcaInactiva.isPresent()) {
            Marca marca = marcaInactiva.get();
            marca.setActivo(true);
            marca.setFechaCommit(LocalDateTime.now());
            return convertToDTO(marcaRepository.save(marca));
        }
        
        Optional<Marca> marcaActiva = marcaRepository.findByNombreIgnoreCaseAndActivoTrue(nombreTrimmed);
        if (marcaActiva.isPresent()) {
            throw new IllegalArgumentException("Ya existe una marca activa con ese nombre");
        }
        
        Marca marca = new Marca();
        marca.setNombre(nombreTrimmed);
        marca.setActivo(true);
        marca.setCreadoPor(authService.getCurrentUser());
        marca.setFechaCommit(LocalDateTime.now());
        
        return convertToDTO(marcaRepository.save(marca));
    }
    
    @Override
    @Transactional
    public MarcaDTO update(Integer id, MarcaUpdateDTO updateDTO) {
        Marca marca = marcaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Marca no encontrada"));
        
        String nombreTrimmed = updateDTO.getNombre().trim();
        
        Optional<Marca> marcaConMismoNombre = marcaRepository.findByNombreIgnoreCaseAndIdNot(nombreTrimmed, id);
        if (marcaConMismoNombre.isPresent()) {
            throw new IllegalArgumentException("Ya existe otra marca con ese nombre");
        }
        
        marca.setNombre(nombreTrimmed);
        marca.setFechaCommit(LocalDateTime.now());
        
        return convertToDTO(marcaRepository.save(marca));
    }
    
    @Override
    @Transactional
    public void delete(Integer id) {
        Marca marca = marcaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Marca no encontrada"));
        
        marca.setActivo(false);
        marca.setFechaCommit(LocalDateTime.now());
        marcaRepository.save(marca);
    }
    
    @Override
    public MarcaDTO convertToDTO(Marca marca) {
        MarcaDTO dto = new MarcaDTO();
        dto.setId(marca.getId());
        dto.setNombre(marca.getNombre());
        dto.setActivo(marca.getActivo());
        dto.setFechaCommit(marca.getFechaCommit());
        
        // Mapear creado por (igual que en AreaServiceImpl)
        if (marca.getCreadoPor() != null) {
            dto.setCreadoPor(marca.getCreadoPor().getPrimerNombre() + " " + marca.getCreadoPor().getPrimerApellido());
        }
        
        return dto;
    }
}