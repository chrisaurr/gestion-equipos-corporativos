package com.gestion.equipos.controller.web;

import com.gestion.equipos.dto.UsuarioEmpleadoCreateDTO;
import com.gestion.equipos.dto.UsuarioEmpleadoDTO;
import com.gestion.equipos.dto.UsuarioEmpleadoUpdateDTO;
import com.gestion.equipos.entity.enums.EstadoUsuario;
import com.gestion.equipos.service.AreaService;
import com.gestion.equipos.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioWebController {

    private final UsuarioService usuarioService;
    private final AreaService areaService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("estados", EstadoUsuario.values());
        return "usuario/list";
    }

    @GetMapping("/crear")
    public String createForm(Model model) {
        model.addAttribute("usuarioEmpleadoCreateDTO", new UsuarioEmpleadoCreateDTO());
        model.addAttribute("estados", EstadoUsuario.values());
        model.addAttribute("areas", areaService.findAllActive());
        return "usuario/create";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute UsuarioEmpleadoCreateDTO createDTO,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        
        if (!createDTO.getPassword().equals(createDTO.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.password.mismatch", 
                                    "Las contraseñas no coinciden");
        }
        
        if (createDTO.getEsEmpleado() && createDTO.getAreaId() == null) {
            bindingResult.rejectValue("areaId", "error.area.required", 
                                    "El área es obligatoria para empleados");
        }
        
        if (createDTO.getEsEmpleado() && (createDTO.getCargo() == null || createDTO.getCargo().trim().isEmpty())) {
            bindingResult.rejectValue("cargo", "error.cargo.required", 
                                    "El cargo es obligatorio para empleados");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("estados", EstadoUsuario.values());
            model.addAttribute("areas", areaService.findAllActive());
            return "usuario/create";
        }

        try {
            usuarioService.saveUsuarioEmpleado(createDTO);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                                               "Usuario creado exitosamente");
            return "redirect:/usuarios";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error al crear usuario: " + e.getMessage());
            model.addAttribute("estados", EstadoUsuario.values());
            model.addAttribute("areas", areaService.findAllActive());
            return "usuario/create";
        }
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Integer id, Model model) {
        return usuarioService.findUsuarioEmpleadoById(id)
                .map(usuarioDTO -> {
                    model.addAttribute("usuario", usuarioDTO);
                    return "usuario/view";
                })
                .orElse("redirect:/usuarios?error=not-found");
    }

    @GetMapping("/{id}/editar")
    public String editForm(@PathVariable Integer id, Model model) {
        return usuarioService.findUsuarioEmpleadoById(id)
                .map(usuarioEmpleadoDTO -> {
                    UsuarioEmpleadoUpdateDTO updateDTO = mapToUpdateDTO(usuarioEmpleadoDTO);
                    model.addAttribute("usuarioEmpleadoUpdateDTO", updateDTO);
                    model.addAttribute("usuarioId", id);
                    model.addAttribute("estados", EstadoUsuario.values());
                    model.addAttribute("areas", areaService.findAllActive());
                    return "usuario/edit";
                })
                .orElse("redirect:/usuarios?error=not-found");
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                        @Valid @ModelAttribute UsuarioEmpleadoUpdateDTO updateDTO,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        
        
        if (updateDTO.isChangePassword()) {
            if (updateDTO.getPassword() == null || updateDTO.getPassword().trim().isEmpty()) {
                bindingResult.rejectValue("password", "error.password.required", 
                                        "La contraseña es obligatoria");
            } else if (!updateDTO.getPassword().equals(updateDTO.getConfirmPassword())) {
                bindingResult.rejectValue("confirmPassword", "error.password.mismatch", 
                                        "Las contraseñas no coinciden");
            }
        }
        
        if (updateDTO.getEsEmpleado() && updateDTO.getAreaId() == null) {
            bindingResult.rejectValue("areaId", "error.area.required", 
                                    "El área es obligatoria para empleados");
        }
        
        if (updateDTO.getEsEmpleado() && (updateDTO.getCargo() == null || updateDTO.getCargo().trim().isEmpty())) {
            bindingResult.rejectValue("cargo", "error.cargo.required", 
                                    "El cargo es obligatorio para empleados");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("usuarioId", id);
            model.addAttribute("estados", EstadoUsuario.values());
            model.addAttribute("areas", areaService.findAllActive());
            return "usuario/edit";
        }

        try {
            usuarioService.updateUsuarioEmpleado(id, updateDTO);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                                               "Usuario actualizado exitosamente");
            return "redirect:/usuarios";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error al actualizar usuario: " + e.getMessage());
            model.addAttribute("usuarioId", id);
            model.addAttribute("estados", EstadoUsuario.values());
            model.addAttribute("areas", areaService.findAllActive());
            return "usuario/edit";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.deleteUsuarioEmpleado(id);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                                               "Usuario eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                                               "Error al eliminar usuario: " + e.getMessage());
        }
        
        return "redirect:/usuarios";
    }

    private final ModelMapper modelMapper;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDate.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text != null && !text.trim().isEmpty()) {
                    setValue(LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                } else {
                    setValue(null);
                }
            }
        });
    }

    private UsuarioEmpleadoUpdateDTO mapToUpdateDTO(UsuarioEmpleadoDTO dto) {
        return modelMapper.map(dto, UsuarioEmpleadoUpdateDTO.class);
    }
}