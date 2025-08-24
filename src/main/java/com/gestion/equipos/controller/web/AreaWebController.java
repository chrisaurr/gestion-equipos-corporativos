package com.gestion.equipos.controller.web;

import com.gestion.equipos.dto.AreaCreateDTO;
import com.gestion.equipos.dto.AreaDTO;
import com.gestion.equipos.dto.AreaUpdateDTO;
import com.gestion.equipos.service.AreaService;
import com.gestion.equipos.service.UsuarioService;
import com.gestion.equipos.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/areas")
@RequiredArgsConstructor
public class AreaWebController {

    private final AreaService areaService;
    private final UsuarioService usuarioService;
    private final AuthService authService;
    private final ModelMapper modelMapper;

    @GetMapping
    public String list(Model model) {
        // Obtener solo usuarios activos para el dropdown
        var usuarios = usuarioService.findAll().stream()
            .filter(u -> u.getEstado() == com.gestion.equipos.entity.enums.EstadoUsuario.ACTIVO)
            .toList();
        model.addAttribute("usuariosActivos", usuarios);
        return "area/list";
    }

    @PostMapping
    @ResponseBody
    public String create(@Valid @ModelAttribute AreaCreateDTO createDTO,
                        BindingResult bindingResult) {
        
        if (bindingResult.hasErrors()) {
            return "Error: " + bindingResult.getFieldError().getDefaultMessage();
        }

        try {
            var area = modelMapper.map(createDTO, com.gestion.equipos.entity.Area.class);
            
            // Auto-set creator from session
            var currentUser = authService.getCurrentUser();
            if (currentUser != null) {
                area.setCreadoPor(currentUser);
            }
            
            // Set optional responsable
            if (createDTO.getResponsableId() != null) {
                var responsable = usuarioService.findById(createDTO.getResponsableId()).orElse(null);
                area.setIdUsuario(responsable);
            }
            
            areaService.save(area);
            return "success";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @PutMapping("/{id}")
    @ResponseBody
    public String update(@PathVariable Integer id,
                        @Valid @ModelAttribute AreaUpdateDTO updateDTO,
                        BindingResult bindingResult) {
        
        if (bindingResult.hasErrors()) {
            return "Error: " + bindingResult.getFieldError().getDefaultMessage();
        }

        try {
            var area = modelMapper.map(updateDTO, com.gestion.equipos.entity.Area.class);
            areaService.update(id, area);
            return "success";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public String delete(@PathVariable Integer id) {
        try {
            areaService.deleteById(id);
            return "success";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/{areaId}/asignar-responsable")
    @ResponseBody
    public String asignarResponsable(@PathVariable Integer areaId,
                                   @RequestParam Integer usuarioId) {
        try {
            areaService.asignarResponsable(areaId, usuarioId);
            return "success";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/{areaId}/remover-responsable")
    @ResponseBody
    public String removerResponsable(@PathVariable Integer areaId) {
        try {
            areaService.removerResponsable(areaId);
            return "success";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}