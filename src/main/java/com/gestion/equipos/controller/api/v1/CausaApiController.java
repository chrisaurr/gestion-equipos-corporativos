package com.gestion.equipos.controller.api.v1;

import com.gestion.equipos.dto.CausaCreateDTO;
import com.gestion.equipos.dto.CausaDTO;
import com.gestion.equipos.dto.CausaUpdateDTO;
import com.gestion.equipos.service.CausaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/causas")
public class CausaApiController {
    
    @Autowired
    private CausaService causaService;
    
    @GetMapping
    public ResponseEntity<Page<CausaDTO>> getCausas(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        
        Sort sortOrder = Sort.by(Sort.Direction.fromString(direction), sort);
        Pageable pageable = PageRequest.of(page, size, sortOrder);
        
        Page<CausaDTO> causas = causaService.findAll(nombre, descripcion, activo, pageable);
        return ResponseEntity.ok(causas);
    }
    
    @GetMapping("/activas")
    public ResponseEntity<List<CausaDTO>> getCausasActivas() {
        List<CausaDTO> causas = causaService.findAllActive();
        return ResponseEntity.ok(causas);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CausaDTO> getCausa(@PathVariable Integer id) {
        Optional<CausaDTO> causa = causaService.findById(id);
        return causa.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<CausaDTO> createCausa(@Valid @RequestBody CausaCreateDTO causaCreateDTO) {
        try {
            CausaDTO causa = causaService.create(causaCreateDTO);
            return ResponseEntity.ok(causa);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CausaDTO> updateCausa(@PathVariable Integer id, @Valid @RequestBody CausaUpdateDTO causaUpdateDTO) {
        try {
            CausaDTO causa = causaService.update(id, causaUpdateDTO);
            return ResponseEntity.ok(causa);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCausa(@PathVariable Integer id) {
        try {
            causaService.delete(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/exists")
    public ResponseEntity<Boolean> existsByNombre(@RequestParam String nombre) {
        boolean exists = causaService.existsByNombre(nombre);
        return ResponseEntity.ok(exists);
    }
}