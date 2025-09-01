package com.gestion.equipos.controller.api.v1;

import com.gestion.equipos.dto.UbicacionCreateDTO;
import com.gestion.equipos.dto.UbicacionDTO;
import com.gestion.equipos.dto.UbicacionFilterDTO;
import com.gestion.equipos.dto.UbicacionUpdateDTO;
import com.gestion.equipos.entity.Ubicacion;
import com.gestion.equipos.service.UbicacionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/ubicaciones")
public class UbicacionApiController {
    
    @Autowired
    private UbicacionService ubicacionService;
    
    @GetMapping
    public ResponseEntity<Page<UbicacionDTO>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String descripcion) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        UbicacionFilterDTO filters = new UbicacionFilterDTO();
        filters.setNombre(nombre);
        filters.setDescripcion(descripcion);
        
        Page<UbicacionDTO> ubicacionDTOPage = ubicacionService.findAllUbicacionDTOWithFilters(filters, pageable);
        
        return ResponseEntity.ok(ubicacionDTOPage);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<UbicacionDTO>> searchGet(UbicacionFilterDTO filterDTO) {
        System.out.println("=== UBICACIONES API SEARCH CALLED ===");
        System.out.println("Filter DTO: " + filterDTO);
        
        Sort sort = Sort.by(
                Sort.Direction.fromString(filterDTO.getDirection()),
                filterDTO.getSort()
        );
        
        Pageable pageable = PageRequest.of(filterDTO.getPage(), filterDTO.getSize(), sort);
        
        Page<UbicacionDTO> ubicacionesPage = ubicacionService.findAllUbicacionDTOWithFilters(filterDTO, pageable);
        
        return ResponseEntity.ok(ubicacionesPage);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<UbicacionDTO>> findAllActive() {
        List<UbicacionDTO> ubicaciones = ubicacionService.findAllActive().stream()
            .map(ubicacionService::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(ubicaciones);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UbicacionDTO> findById(@PathVariable Integer id) {
        UbicacionDTO ubicacionDTO = ubicacionService.findUbicacionDTOById(id)
                .orElseThrow(() -> new RuntimeException("Ubicación no encontrada"));
        
        return ResponseEntity.ok(ubicacionDTO);
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody UbicacionCreateDTO createDTO) {
        try {
            UbicacionDTO ubicacion = ubicacionService.create(createDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Ubicación creada exitosamente");
            response.put("data", ubicacion);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Integer id, @Valid @RequestBody UbicacionUpdateDTO updateDTO) {
        try {
            UbicacionDTO ubicacion = ubicacionService.update(id, updateDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Ubicación actualizada exitosamente");
            response.put("data", ubicacion);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer id) {
        try {
            ubicacionService.delete(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Ubicación eliminada exitosamente");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}