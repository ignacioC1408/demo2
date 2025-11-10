package com.example.puntofacil.demo.controller;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.puntofacil.demo.entity.Empleado;
import com.example.puntofacil.demo.repository.EmpleadoRepository;

@Controller
public class AuthController {

    private final EmpleadoRepository empleadoRepository;

    public AuthController(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @GetMapping("/forgot")
    public String forgotForm() {
        return "forgot";
    }

    @PostMapping("/forgot")
    public String forgotSubmit(@RequestParam String usernameOrEmail, Model model) {
        Optional<Empleado> opt = empleadoRepository.findByUsername(usernameOrEmail);
        if (opt.isEmpty()) {
            model.addAttribute("message", "Usuario no encontrado");
            return "forgot";
        }

        Empleado emp = opt.get();
        String token = generateToken();
        emp.setTokenRecuperacion(token);
        empleadoRepository.save(emp);

        String resetLink = "/reset?token=" + token;
        model.addAttribute("message", "Token generado (modo dev). Copie el link:");
        model.addAttribute("resetLink", resetLink);
        return "forgot";
    }

    @GetMapping("/reset")
    public String resetForm(@RequestParam(required = false) String token, Model model) {
        model.addAttribute("token", token);
        return "reset";
    }

    @PostMapping("/reset")
    public String resetSubmit(@RequestParam String token,
                              @RequestParam String newPassword,
                              Model model) {
        Optional<Empleado> opt = empleadoRepository.findByTokenRecuperacion(token);
        if (opt.isEmpty()) {
            model.addAttribute("error", "Token inválido");
            return "reset";
        }

        Empleado emp = opt.get();
        emp.setPassword(newPassword); // 👈 usamos password, no passwordHash
        emp.setTokenRecuperacion(null); // limpiar token
        empleadoRepository.save(emp);

        model.addAttribute("message", "Contraseña cambiada. Inicie sesión.");
        return "login";
    }

    private String generateToken() {
        byte[] random = new byte[24];
        new SecureRandom().nextBytes(random);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(random);
    }
}
