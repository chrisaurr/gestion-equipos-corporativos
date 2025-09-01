package com.gestion.equipos.controller.web;

import com.gestion.equipos.dto.UbicacionCreateDTO;
import com.gestion.equipos.dto.UbicacionUpdateDTO;
import com.gestion.equipos.service.UbicacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ubicaciones")
@RequiredArgsConstructor
public class UbicacionWebController {

    private final UbicacionService ubicacionService;

    @GetMapping
    public String list(Model model) {
        return "ubicacion/list";
    }

    @PostMapping
    @ResponseBody
    public String create(@Valid @ModelAttribute UbicacionCreateDTO createDTO,
                        BindingResult bindingResult) {
        
        if (bindingResult.hasErrors()) {
            return "Error: " + bindingResult.getFieldError().getDefaultMessage();
        }

        try {
            ubicacionService.create(createDTO);
            return "success";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @PutMapping("/{id}")
    @ResponseBody
    public String update(@PathVariable Integer id,
                        @Valid @ModelAttribute UbicacionUpdateDTO updateDTO,
                        BindingResult bindingResult) {
        
        if (bindingResult.hasErrors()) {
            return "Error: " + bindingResult.getFieldError().getDefaultMessage();
        }

        try {
            ubicacionService.update(id, updateDTO);
            return "success";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public String delete(@PathVariable Integer id) {
        try {
            ubicacionService.delete(id);
            return "success";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}