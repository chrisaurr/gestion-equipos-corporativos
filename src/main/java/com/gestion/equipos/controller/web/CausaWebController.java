package com.gestion.equipos.controller.web;

import com.gestion.equipos.dto.CausaCreateDTO;
import com.gestion.equipos.dto.CausaUpdateDTO;
import com.gestion.equipos.service.CausaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/causas")
@RequiredArgsConstructor
public class CausaWebController {

    private final CausaService causaService;

    @GetMapping
    public String list(Model model) {
        return "causa/list";
    }

    @PostMapping
    @ResponseBody
    public String create(@Valid @ModelAttribute CausaCreateDTO createDTO,
                        BindingResult bindingResult) {
        
        if (bindingResult.hasErrors()) {
            return "Error: " + bindingResult.getFieldError().getDefaultMessage();
        }

        try {
            causaService.create(createDTO);
            return "success";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @PutMapping("/{id}")
    @ResponseBody
    public String update(@PathVariable Integer id,
                        @Valid @ModelAttribute CausaUpdateDTO updateDTO,
                        BindingResult bindingResult) {
        
        if (bindingResult.hasErrors()) {
            return "Error: " + bindingResult.getFieldError().getDefaultMessage();
        }

        try {
            causaService.update(id, updateDTO);
            return "success";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public String delete(@PathVariable Integer id) {
        try {
            causaService.delete(id);
            return "success";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}