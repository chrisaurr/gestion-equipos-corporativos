package com.gestion.equipos.controller.web;

import com.gestion.equipos.dto.EquipoCreateDTO;
import com.gestion.equipos.dto.EquipoDTO;
import com.gestion.equipos.dto.EquipoUpdateDTO;
import com.gestion.equipos.entity.enums.Alimentacion;
import com.gestion.equipos.entity.enums.Conectividad;
import com.gestion.equipos.entity.enums.Operador;
import com.gestion.equipos.service.EquipoService;
import com.gestion.equipos.service.MarcaService;
import com.gestion.equipos.service.UbicacionService;
import com.gestion.equipos.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/equipos")
@RequiredArgsConstructor
public class EquipoWebController {
    
    private final EquipoService equipoService;
    private final MarcaService marcaService;
    private final UbicacionService ubicacionService;
    private final UsuarioService usuarioService;
    
    @GetMapping
    public String list(Model model) {
        // Obtener solo usuarios activos para el dropdown - EXACTLY like área
        var usuarios = usuarioService.findAll().stream()
            .filter(u -> u.getEstado() == com.gestion.equipos.entity.enums.EstadoUsuario.ACTIVO)
            .toList();
        model.addAttribute("usuariosActivos", usuarios);
        return "equipo/list";
    }
    
    @GetMapping("/crear")
    public String showCreateForm(Model model) {
        model.addAttribute("equipoCreateDTO", new EquipoCreateDTO());
        model.addAttribute("marcas", marcaService.findAllActive());
        model.addAttribute("ubicaciones", ubicacionService.findAllActive());
        model.addAttribute("alimentaciones", Alimentacion.values());
        model.addAttribute("conectividades", Conectividad.values());
        model.addAttribute("operadores", Operador.values());
        return "equipo/create";
    }
    
    @PostMapping
    public String create(@Valid @ModelAttribute EquipoCreateDTO equipoCreateDTO, 
                        BindingResult bindingResult, 
                        Model model, 
                        RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Por favor corrige los errores en el formulario");
            model.addAttribute("marcas", marcaService.findAllActive());
            model.addAttribute("ubicaciones", ubicacionService.findAllActive());
            model.addAttribute("alimentaciones", Alimentacion.values());
            model.addAttribute("conectividades", Conectividad.values());
            model.addAttribute("operadores", Operador.values());
            return "equipo/create";
        }
        
        try {
            equipoCreateDTO.validate();
            EquipoDTO createdEquipo = equipoService.create(equipoCreateDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Equipo creado exitosamente con ID: " + createdEquipo.getIdentificador());
            return "redirect:/equipos";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("marcas", marcaService.findAllActive());
            model.addAttribute("ubicaciones", ubicacionService.findAllActive());
            model.addAttribute("alimentaciones", Alimentacion.values());
            model.addAttribute("conectividades", Conectividad.values());
            model.addAttribute("operadores", Operador.values());
            return "equipo/create";
        }
    }
    
    @GetMapping("/{id}")
    public String view(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            EquipoDTO equipo = equipoService.findEquipoDTOById(id)
                    .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
            model.addAttribute("equipo", equipo);
            return "equipo/view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/equipos";
        }
    }
    
    @GetMapping("/{id}/editar")
    public String showEditForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            EquipoDTO equipo = equipoService.findEquipoDTOById(id)
                    .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
            
            EquipoUpdateDTO updateDTO = new EquipoUpdateDTO();
            updateDTO.setNombre(equipo.getNombre());
            updateDTO.setMarcaId(equipo.getMarcaId());
            updateDTO.setUbicacionId(equipo.getUbicacionId());
            updateDTO.setColor(equipo.getColor());
            updateDTO.setSerie(equipo.getSerie());
            updateDTO.setValor(equipo.getValor());
            updateDTO.setTipoAlimentacion(equipo.getTipoAlimentacion());
            updateDTO.setExtras(equipo.getExtras());
            updateDTO.setTipoEquipo(equipo.getTipoEquipo());
            
            model.addAttribute("equipoUpdateDTO", updateDTO);
            model.addAttribute("equipo", equipo);
            model.addAttribute("marcas", marcaService.findAllActive());
            model.addAttribute("ubicaciones", ubicacionService.findAllActive());
            model.addAttribute("alimentaciones", Alimentacion.values());
            model.addAttribute("conectividades", Conectividad.values());
            model.addAttribute("operadores", Operador.values());
            return "equipo/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/equipos";
        }
    }
    
    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                        @Valid @ModelAttribute EquipoUpdateDTO equipoUpdateDTO,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Por favor corrige los errores en el formulario");
            model.addAttribute("marcas", marcaService.findAllActive());
            model.addAttribute("ubicaciones", ubicacionService.findAllActive());
            model.addAttribute("alimentaciones", Alimentacion.values());
            model.addAttribute("conectividades", Conectividad.values());
            model.addAttribute("operadores", Operador.values());
            return "equipo/edit";
        }
        
        try {
            equipoUpdateDTO.validate();
            EquipoDTO updatedEquipo = equipoService.update(id, equipoUpdateDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Equipo actualizado exitosamente");
            return "redirect:/equipos";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("marcas", marcaService.findAllActive());
            model.addAttribute("ubicaciones", ubicacionService.findAllActive());
            model.addAttribute("alimentaciones", Alimentacion.values());
            model.addAttribute("conectividades", Conectividad.values());
            model.addAttribute("operadores", Operador.values());
            return "equipo/edit";
        }
    }
    
}