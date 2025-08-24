package com.gestion.equipos.controller;

// import com.gestion.equipos.entity.User;
// import com.gestion.equipos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-adminlte3
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 25/11/20
 * Time: 08.41
 */
// @Controller - Comentado temporalmente hasta migrar autenticación
public class AdminPageController {
    // @Autowired - Comentado temporalmente hasta migrar autenticación
    // private UserRepository userRepository;

    // @GetMapping({"/user/list", "/admin/user"})
    // public String listUser() {
    //     return "user-list";
    // }

    // Comentado temporalmente hasta migrar autenticación
    // @GetMapping("/user/list2")
    // public String listUser2(Model model) {
    //     model.addAttribute("users", userRepository.findAll());
    //     return "user-list2";
    // }

    // @GetMapping("/user/add")
    // public String showFormUser(Model model) {
    //     model.addAttribute("user", new User());
    //     return "user-add";
    // }

    // @PostMapping("/user/add")
    // public String addUser(Model model, User user) {
    //     userRepository.save(user);
    //     model.addAttribute("user", new User());
    //     return "user-list";
    // }

}
