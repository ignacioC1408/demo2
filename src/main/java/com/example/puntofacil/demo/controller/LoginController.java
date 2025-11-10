package com.example.puntofacil.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login"; // login.html para EMPLEADOS
    }

    @GetMapping("/logout-success")
    public String logout() {
        return "redirect:/login?logout";
    }
}