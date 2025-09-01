package com.gestion.equipos.controller.api.v1;

import com.gestion.equipos.dto.*;
import com.gestion.equipos.entity.enums.EstadoEquipo;
import com.gestion.equipos.entity.enums.MotivoCambio;
import com.gestion.equipos.entity.enums.TipoEquipo;
import com.gestion.equipos.service.EquipoAsignacionService;
import com.gestion.equipos.service.EquipoService;
import com.gestion.equipos.service.HistorialAsignacionService;
import com.gestion.equipos.service.ReporteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/equipos")
@RequiredArgsConstructor
public class EquipoApiController {
    
    private final EquipoService equipoService;
    private final EquipoAsignacionService equipoAsignacionService;
    private final HistorialAsignacionService historialAsignacionService;
    private final ReporteService reporteService;
    
    @GetMapping
    public ResponseEntity<Page<EquipoDTO>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "identificador") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String identificador,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String serie,
            @RequestParam(required = false) Integer marcaId,
            @RequestParam(required = false) TipoEquipo tipoEquipo,
            @RequestParam(required = false) EstadoEquipo estado,
            @RequestParam(required = false) Integer ubicacionId,
            @RequestParam(required = false) Integer usuarioAsignadoId,
            @RequestParam(required = false) Boolean asignado) {
        
        EquipoFilterDTO filters = new EquipoFilterDTO();
        filters.setIdentificador(identificador);
        filters.setNombre(nombre);
        filters.setSerie(serie);
        filters.setMarcaId(marcaId);
        filters.setTipoEquipo(tipoEquipo);
        filters.setEstado(estado);
        filters.setUbicacionId(ubicacionId);
        filters.setUsuarioAsignadoId(usuarioAsignadoId);
        filters.setAsignado(asignado);
        
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<EquipoDTO> result = equipoService.findAllEquipoDTOWithFilters(filters, pageable);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EquipoDTO> findById(@PathVariable Integer id) {
        return equipoService.findEquipoDTOById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<EquipoDTO> create(@Valid @RequestBody EquipoCreateDTO createDTO) {
        try {
            createDTO.validate();
            EquipoDTO created = equipoService.create(createDTO);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<EquipoDTO> update(@PathVariable Integer id, @Valid @RequestBody EquipoUpdateDTO updateDTO) {
        try {
            updateDTO.validate();
            EquipoDTO updated = equipoService.update(id, updateDTO);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            equipoService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/estado")
    public ResponseEntity<EquipoDTO> cambiarEstado(
            @PathVariable Integer id, 
            @RequestParam EstadoEquipo estado, 
            @RequestParam(required = false) String motivo) {
        try {
            EquipoDTO updated = equipoService.cambiarEstado(id, estado, motivo);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{id}/asignar")
    public ResponseEntity<String> asignarEquipo(
            @PathVariable Integer id, 
            @RequestParam Integer usuarioId, 
            @RequestParam(required = false) String motivo) {
        try {
            EquipoDTO assigned = equipoAsignacionService.asignarEquipo(id, usuarioId, motivo);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/{id}/asignar-con-motivo")
    public ResponseEntity<String> asignarEquipoConMotivo(
            @PathVariable Integer id,
            @RequestParam Integer usuarioId,
            @RequestParam MotivoCambio motivoCambio,
            @RequestParam(required = false) String motivo) {
        try {
            EquipoDTO assigned = equipoAsignacionService.asignarEquipoConMotivo(id, usuarioId, motivoCambio, motivo);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/{id}/liberar")
    public ResponseEntity<EquipoDTO> liberarEquipo(
            @PathVariable Integer id, 
            @RequestParam MotivoCambio motivoCambio, 
            @RequestParam(required = false) String motivo) {
        try {
            EquipoDTO released = equipoAsignacionService.liberarEquipo(id, motivoCambio, motivo);
            return ResponseEntity.ok(released);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    
    @GetMapping("/tipos/{tipo}")
    public ResponseEntity<List<EquipoDTO>> findByTipo(@PathVariable TipoEquipo tipo) {
        List<EquipoDTO> equipos = equipoService.findByTipoEquipo(tipo);
        return ResponseEntity.ok(equipos);
    }
    
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<EquipoDTO>> findByUsuario(@PathVariable Integer usuarioId) {
        List<EquipoDTO> equipos = equipoService.findEquiposByUsuario(usuarioId);
        return ResponseEntity.ok(equipos);
    }
    
    @GetMapping("/libres")
    public ResponseEntity<List<EquipoDTO>> findEquiposLibres() {
        List<EquipoDTO> equipos = equipoService.findEquiposLibres();
        return ResponseEntity.ok(equipos);
    }
    
    @PostMapping("/{id}/historial/{historialId}/confirmar")
    public ResponseEntity<Void> confirmarDevolucion(@PathVariable Integer id, @PathVariable Integer historialId) {
        try {
            historialAsignacionService.confirmarDevolucion(historialId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    
    @GetMapping("/{id}/historial")
    public ResponseEntity<?> getHistorial(@PathVariable Integer id) {
        try {
            var historial = historialAsignacionService.findHistorialByEquipo(id);
            return ResponseEntity.ok(historial);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}/tiene-historial")
    public ResponseEntity<Boolean> tieneHistorial(@PathVariable Integer id) {
        try {
            boolean tieneHistorial = historialAsignacionService.existsByIdEquipo_Id(id);
            return ResponseEntity.ok(tieneHistorial);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }
    
    @GetMapping("/{id}/devolucion-pendiente")
    public ResponseEntity<?> getDevolucionPendiente(@PathVariable Integer id) {
        try {
            var pendingReturn = historialAsignacionService.findPendingReturnByEquipo(id);
            if (pendingReturn.isPresent()) {
                var dto = com.gestion.equipos.dto.HistorialPendienteDTO.fromEntity(pendingReturn.get());
                return ResponseEntity.ok(dto);
            } else {
                return ResponseEntity.ok(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    
    @GetMapping("/{id}/reportes-abiertos")
    public ResponseEntity<Boolean> tieneReportesAbiertos(@PathVariable Integer id) {
        try {
            boolean tieneReportesAbiertos = reporteService.equipoTieneReportesAbiertos(id);
            return ResponseEntity.ok(tieneReportesAbiertos);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }
}