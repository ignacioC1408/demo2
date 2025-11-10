package com.example.puntofacil.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // NUEVO: Redirigir la raíz "/" al registro de usuarios
    @GetMapping("/")
    public String root() {
        return "redirect:/usuario/registro";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("mensaje", "Bienvenido a Punto Fácil");
        return "home";
    }
}