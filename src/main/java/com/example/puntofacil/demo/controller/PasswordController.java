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
public class PasswordController {

    private final EmpleadoRepository empleadoRepository;

    public PasswordController(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @GetMapping("/cambiar-password")
    public String mostrarFormulario() {
        return "cambiar_password";
    }

    @PostMapping("/cambiar-password")
    public String cambiarPassword(@RequestParam String actual,
                                  @RequestParam String nueva,
                                  @RequestParam String username,
                                  Model model) {

        Optional<Empleado> optEmpleado = empleadoRepository.findByUsername(username);

        if (optEmpleado.isPresent()) {
            Empleado empleado = optEmpleado.get();
            if (empleado.getPassword().equals(actual)) {
                empleado.setPassword(nueva);
                empleadoRepository.save(empleado);
                model.addAttribute("mensaje", "Contraseña actualizada correctamente.");
            } else {
                model.addAttribute("mensaje", "La contraseña actual no es correcta.");
            }
        } else {
            model.addAttribute("mensaje", "Usuario no encontrado.");
        }

        return "cambiar_password";
    }
}
