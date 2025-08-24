package com.gestion.equipos.controller.api;

import com.gestion.equipos.dto.AreaDTO;
import com.gestion.equipos.dto.AreaFilterDTO;
import com.gestion.equipos.entity.Area;
import com.gestion.equipos.service.AreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/areas")
@RequiredArgsConstructor
public class AreaApiController {

    private final AreaService areaService;

    @PostMapping("/search")
    public Page<Area> search(@RequestBody AreaFilterDTO filterDTO) {
        
        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(filterDTO.getDirection()) ? 
                        Sort.Direction.DESC : Sort.Direction.ASC, 
                filterDTO.getSort()
        );
        
        Pageable pageable = PageRequest.of(filterDTO.getPage(), filterDTO.getSize(), sort);
        
        return areaService.findAllWithFilters(
                filterDTO.getNombre(),
                filterDTO.getActivo(),
                filterDTO.getResponsableId(),
                pageable
        );
    }

    @GetMapping("/search")
    public ResponseEntity<Page<AreaDTO>> searchGet(AreaFilterDTO filterDTO) {
        System.out.println("=== AREAS API SEARCH CALLED ===");
        System.out.println("Filter DTO: " + filterDTO);
        
        Sort sort = Sort.by(
                Sort.Direction.fromString(filterDTO.getDirection()),
                filterDTO.getSort()
        );
        
        Pageable pageable = PageRequest.of(filterDTO.getPage(), filterDTO.getSize(), sort);
        
        Page<AreaDTO> areasPage = areaService.findAllAreaDTOWithFilters(
                filterDTO.getNombre(),
                filterDTO.getActivo(),
                filterDTO.getResponsableId(),
                pageable
        );
        
        return ResponseEntity.ok(areasPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AreaDTO> findById(@PathVariable Integer id) {
        AreaDTO areaDTO = areaService.findAreaDTOById(id)
                .orElseThrow(() -> new RuntimeException("Área no encontrada"));
        
        return ResponseEntity.ok(areaDTO);
    }
}