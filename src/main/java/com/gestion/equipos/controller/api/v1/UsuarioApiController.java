package com.gestion.equipos.controller.api.v1;

import com.gestion.equipos.dto.EquipoDTO;
import com.gestion.equipos.dto.UsuarioEmpleadoDTO;
import com.gestion.equipos.dto.UsuarioFilterDTO;
import com.gestion.equipos.service.EquipoService;
import com.gestion.equipos.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioApiController {

    private final UsuarioService usuarioService;
    private final EquipoService equipoService;

    @GetMapping("/search")
    public ResponseEntity<Page<UsuarioEmpleadoDTO>> search(UsuarioFilterDTO filterDTO) {
        System.out.println("=== API SEARCH CALLED ===");
        System.out.println("Filter DTO: " + filterDTO);
        
        Sort sort = Sort.by(Sort.Direction.fromString(filterDTO.getDirection()), filterDTO.getSort());
        PageRequest pageRequest = PageRequest.of(filterDTO.getPage(), filterDTO.getSize(), sort);
        
        Page<UsuarioEmpleadoDTO> usuariosPage = usuarioService.findAllUsuarioEmpleadoWithFilters(
                filterDTO.getCodigo(),
                filterDTO.getUsuario(),
                filterDTO.getPrimerNombre(),
                filterDTO.getPrimerApellido(),
                filterDTO.getEstado(),
                filterDTO.getFechaIngresoDesde(),
                filterDTO.getFechaIngresoHasta(),
                filterDTO.getIsAdmin(),
                filterDTO.getEsEmpleado(),
                filterDTO.getAreaId(),
                pageRequest
        );
        
        return ResponseEntity.ok(usuariosPage);
    }

    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<UsuarioEmpleadoDTO> findById(@PathVariable Integer id) {
        return usuarioService.findUsuarioEmpleadoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/exists/usuario/{usuario}")
    public ResponseEntity<Boolean> existsByUsuario(@PathVariable String usuario) {
        boolean exists = usuarioService.existsByUsuario(usuario);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/codigo/{codigo}")
    public ResponseEntity<Boolean> existsByCodigo(@PathVariable String codigo) {
        boolean exists = usuarioService.existsByCodigo(codigo);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/usuario/{usuario}/exclude/{id}")
    public ResponseEntity<Boolean> existsByUsuarioExcludeId(@PathVariable String usuario, @PathVariable Integer id) {
        boolean exists = usuarioService.existsByUsuarioAndIdNot(usuario, id);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/codigo/{codigo}/exclude/{id}")
    public ResponseEntity<Boolean> existsByCodigoExcludeId(@PathVariable String codigo, @PathVariable Integer id) {
        boolean exists = usuarioService.existsByCodigoAndIdNot(codigo, id);
        return ResponseEntity.ok(exists);
    }
    
    @GetMapping("/{usuarioId:[0-9]+}/historial-empleado")
    public ResponseEntity<?> getHistorialEmpleado(@PathVariable Integer usuarioId) {
        try {
            // Verificar si el usuario es empleado
            var usuarioEmpleado = usuarioService.findUsuarioEmpleadoById(usuarioId);
            if (usuarioEmpleado.isEmpty() || !usuarioEmpleado.get().getEsEmpleado()) {
                return ResponseEntity.badRequest().body("El usuario no es empleado");
            }

            // Obtener historial
            var historial = usuarioService.getHistorialEmpleado(usuarioId);
            return ResponseEntity.ok(historial);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{usuarioId:[0-9]+}/equipos")
    public ResponseEntity<List<EquipoDTO>> getEquiposUsuario(@PathVariable Integer usuarioId) {
        try {
            List<EquipoDTO> equipos = equipoService.findEquiposByUsuario(usuarioId);
            return ResponseEntity.ok(equipos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/auth-status")
    public ResponseEntity<Map<String, Boolean>> checkAuthenticationStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);

        Map<String, Boolean> response = new HashMap<>();
        response.put("authenticated", isAuthenticated);
        return ResponseEntity.ok(response);
    }
}