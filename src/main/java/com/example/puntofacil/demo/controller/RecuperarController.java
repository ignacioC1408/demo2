package com.example.puntofacil.demo.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.puntofacil.demo.entity.Empleado;
import com.example.puntofacil.demo.repository.EmpleadoRepository;

@Controller
public class RecuperarController {

    private final EmpleadoRepository empleadoRepository;

    public RecuperarController(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @GetMapping("/recuperar")
    public String mostrarFormulario() {
        return "recuperar";
    }

    @PostMapping("/recuperar")
    public String procesarRecuperacion(@RequestParam String username, Model model) {
        Optional<Empleado> optEmpleado = empleadoRepository.findByUsername(username);

        if (optEmpleado.isPresent()) {
            model.addAttribute("mensaje", "Usuario encontrado. Contacta con el administrador para restablecer tu contraseña.");
        } else {
            model.addAttribute("error", "Usuario no encontrado.");
        }

        return "recuperar";
    }
}

