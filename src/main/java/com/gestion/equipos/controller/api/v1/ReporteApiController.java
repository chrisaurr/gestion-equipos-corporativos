package com.gestion.equipos.controller.api.v1;

import com.gestion.equipos.dto.ReporteDTO;
import com.gestion.equipos.dto.ReporteFilterDTO;
import com.gestion.equipos.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
public class ReporteApiController {

    private final ReporteService reporteService;

    @GetMapping("/search")
    public ResponseEntity<Page<ReporteDTO>> search(
            @RequestParam(required = false) String observacion,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Integer causaId,
            @RequestParam(required = false) Integer equipoId,
            @RequestParam(required = false) Integer usuarioId,
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaCommit") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        ReporteFilterDTO filters = new ReporteFilterDTO();
        filters.setObservacion(observacion);
        filters.setEstado(estado);
        filters.setCausaId(causaId);
        filters.setEquipoId(equipoId);
        filters.setUsuarioId(usuarioId);
        filters.setFechaDesde(fechaDesde);
        filters.setFechaHasta(fechaHasta);

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<ReporteDTO> result = reporteService.findAllWithFilters(filters, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReporteDTO> getById(@PathVariable Integer id) {
        return reporteService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}