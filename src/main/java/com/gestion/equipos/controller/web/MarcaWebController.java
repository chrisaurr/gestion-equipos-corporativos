package com.gestion.equipos.controller.web;

import com.gestion.equipos.dto.MarcaCreateDTO;
import com.gestion.equipos.dto.MarcaUpdateDTO;
import com.gestion.equipos.service.MarcaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/marcas")
@RequiredArgsConstructor
public class MarcaWebController {

    private final MarcaService marcaService;

    @GetMapping
    public String list(Model model) {
        return "marca/list";
    }

    @PostMapping
    @ResponseBody
    public String create(@Valid @ModelAttribute MarcaCreateDTO createDTO,
                        BindingResult bindingResult) {
        
        if (bindingResult.hasErrors()) {
            return "Error: " + bindingResult.getFieldError().getDefaultMessage();
        }

        try {
            marcaService.create(createDTO);
            return "success";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @PutMapping("/{id}")
    @ResponseBody
    public String update(@PathVariable Integer id,
                        @Valid @ModelAttribute MarcaUpdateDTO updateDTO,
                        BindingResult bindingResult) {
        
        if (bindingResult.hasErrors()) {
            return "Error: " + bindingResult.getFieldError().getDefaultMessage();
        }

        try {
            marcaService.update(id, updateDTO);
            return "success";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public String delete(@PathVariable Integer id) {
        try {
            marcaService.delete(id);
            return "success";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}