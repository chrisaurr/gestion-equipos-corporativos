package com.gestion.equipos.controller.api.v1;

import com.gestion.equipos.dto.MarcaCreateDTO;
import com.gestion.equipos.dto.MarcaDTO;
import com.gestion.equipos.dto.MarcaFilterDTO;
import com.gestion.equipos.dto.MarcaUpdateDTO;
import com.gestion.equipos.entity.Marca;
import com.gestion.equipos.service.MarcaService;
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
@RequestMapping("/api/v1/marcas")
public class MarcaApiController {
    
    @Autowired
    private MarcaService marcaService;
    
    @GetMapping
    public ResponseEntity<Page<MarcaDTO>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String nombre) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        MarcaFilterDTO filters = new MarcaFilterDTO();
        filters.setNombre(nombre);
        
        Page<MarcaDTO> marcaDTOPage = marcaService.findAllMarcaDTOWithFilters(filters, pageable);
        
        return ResponseEntity.ok(marcaDTOPage);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<MarcaDTO>> searchGet(MarcaFilterDTO filterDTO) {
        System.out.println("=== MARCAS API SEARCH CALLED ===");
        System.out.println("Filter DTO: " + filterDTO);
        
        Sort sort = Sort.by(
                Sort.Direction.fromString(filterDTO.getDirection()),
                filterDTO.getSort()
        );
        
        Pageable pageable = PageRequest.of(filterDTO.getPage(), filterDTO.getSize(), sort);
        
        Page<MarcaDTO> marcasPage = marcaService.findAllMarcaDTOWithFilters(filterDTO, pageable);
        
        return ResponseEntity.ok(marcasPage);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<MarcaDTO>> findAllActive() {
        List<MarcaDTO> marcas = marcaService.findAllActive().stream()
            .map(marcaService::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(marcas);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MarcaDTO> findById(@PathVariable Integer id) {
        MarcaDTO marcaDTO = marcaService.findMarcaDTOById(id)
                .orElseThrow(() -> new RuntimeException("Marca no encontrada"));
        
        return ResponseEntity.ok(marcaDTO);
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody MarcaCreateDTO createDTO) {
        try {
            MarcaDTO marca = marcaService.create(createDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Marca creada exitosamente");
            response.put("data", marca);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Integer id, @Valid @RequestBody MarcaUpdateDTO updateDTO) {
        try {
            MarcaDTO marca = marcaService.update(id, updateDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Marca actualizada exitosamente");
            response.put("data", marca);
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
            marcaService.delete(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Marca eliminada exitosamente");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}