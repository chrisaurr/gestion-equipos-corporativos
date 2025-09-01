package com.gestion.equipos.controller.web;

import com.gestion.equipos.dto.ReporteCreateDTO;
import com.gestion.equipos.dto.ReporteUpdateDTO;
import com.gestion.equipos.entity.enums.EstadoReporte;
import com.gestion.equipos.service.CausaService;
import com.gestion.equipos.service.EquipoService;
import com.gestion.equipos.service.ReporteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteWebController {

    private final ReporteService reporteService;
    private final CausaService causaService;
    private final EquipoService equipoService;

    @GetMapping
    public String list(Model model) {
        return "reporte/list";
    }

    @GetMapping("/crear")
    public String create(Model model) {
        model.addAttribute("reporteCreateDTO", new ReporteCreateDTO());
        model.addAttribute("causas", causaService.findAllActive());
        model.addAttribute("equipos", equipoService.findAllActive());
        return "reporte/create";
    }

    @PostMapping("/crear")
    public String create(@Valid @ModelAttribute ReporteCreateDTO reporteCreateDTO,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("causas", causaService.findAllActive());
            model.addAttribute("equipos", equipoService.findAllActive());
            return "reporte/create";
        }

        try {
            reporteService.create(reporteCreateDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Reporte creado exitosamente");
            return "redirect:/reportes";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error: " + e.getMessage());
            model.addAttribute("causas", causaService.findAllActive());
            model.addAttribute("equipos", equipoService.findAllActive());
            return "reporte/create";
        }
    }

    @GetMapping("/{id}/editar")
    public String edit(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        return reporteService.findById(id)
                .map(reporte -> {
                    ReporteUpdateDTO updateDTO = new ReporteUpdateDTO();
                    updateDTO.setObservacion(reporte.getObservacion());
                    updateDTO.setCausaId(reporte.getCausaId());
                    updateDTO.setEquipoId(reporte.getEquipoId());
                    updateDTO.setEstado(reporte.getEstado());
                    
                    model.addAttribute("reporteUpdateDTO", updateDTO);
                    model.addAttribute("reporte", reporte);
                    model.addAttribute("causas", causaService.findAllActive());
                    model.addAttribute("equipos", equipoService.findAllActive());
                    model.addAttribute("estados", EstadoReporte.values());
                    return "reporte/edit";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Reporte no encontrado");
                    return "redirect:/reportes";
                });
    }

    @PostMapping("/{id}/editar")
    public String edit(@PathVariable Integer id,
                      @Valid @ModelAttribute ReporteUpdateDTO reporteUpdateDTO,
                      BindingResult bindingResult,
                      Model model,
                      RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            return reporteService.findById(id)
                    .map(reporte -> {
                        model.addAttribute("reporte", reporte);
                        model.addAttribute("causas", causaService.findAllActive());
                        model.addAttribute("equipos", equipoService.findAllActive());
                        model.addAttribute("estados", EstadoReporte.values());
                        return "reporte/edit";
                    })
                    .orElseGet(() -> {
                        redirectAttributes.addFlashAttribute("errorMessage", "Reporte no encontrado");
                        return "redirect:/reportes";
                    });
        }

        try {
            reporteService.update(id, reporteUpdateDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Reporte actualizado exitosamente");
            return "redirect:/reportes";
        } catch (Exception e) {
            return reporteService.findById(id)
                    .map(reporte -> {
                        model.addAttribute("errorMessage", "Error: " + e.getMessage());
                        model.addAttribute("reporte", reporte);
                        model.addAttribute("causas", causaService.findAllActive());
                        model.addAttribute("equipos", equipoService.findAllActive());
                        model.addAttribute("estados", EstadoReporte.values());
                        return "reporte/edit";
                    })
                    .orElseGet(() -> {
                        redirectAttributes.addFlashAttribute("errorMessage", "Reporte no encontrado");
                        return "redirect:/reportes";
                    });
        }
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        return reporteService.findById(id)
                .map(reporte -> {
                    model.addAttribute("reporte", reporte);
                    return "reporte/view";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Reporte no encontrado");
                    return "redirect:/reportes";
                });
    }

    @PostMapping("/{id}/eliminar")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            reporteService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Reporte eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/reportes";
    }

    @PostMapping("/{id}/cambiar-estado")
    public String cambiarEstado(@PathVariable Integer id, 
                               @RequestParam EstadoReporte nuevoEstado,
                               RedirectAttributes redirectAttributes) {
        try {
            reporteService.cambiarEstado(id, nuevoEstado);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Estado del reporte cambiado a " + nuevoEstado + " exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/reportes/" + id;
    }
}