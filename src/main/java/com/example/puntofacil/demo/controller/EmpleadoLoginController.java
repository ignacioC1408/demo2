package com.example.puntofacil.demo.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.puntofacil.demo.entity.Empleado;
import com.example.puntofacil.demo.repository.EmpleadoRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/empleado")
public class EmpleadoLoginController {

    private final EmpleadoRepository empleadoRepository;

    public EmpleadoLoginController(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        return "empleado-login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String username,
                                @RequestParam String password,
                                HttpSession session,
                                Model model) {

        Optional<Empleado> optEmpleado = empleadoRepository.findByUsername(username);

        if (optEmpleado.isEmpty()) {
            model.addAttribute("error", "Usuario no encontrado");
            return "empleado-login";
        }

        Empleado empleado = optEmpleado.get();

        // 🔐 Verificación básica
        if (!empleado.getPassword().equals(password)) {
            model.addAttribute("error", "Contraseña incorrecta");
            return "empleado-login";
        }

        // Guardar sesión
        session.setAttribute("empleado", empleado);

        // ✅ REDIRECCIONES CORREGIDAS - ahora apuntan a las rutas correctas
        switch (empleado.getRol().toUpperCase()) {
            case "DUEÑO":
                return "redirect:/empleado/panel/admin";    // ← CORREGIDO
            case "CAJERA":
                return "redirect:/empleado/panel/cajera";   // ← CORREGIDO
            case "VENDEDOR":
                return "redirect:/empleado/panel/vendedor"; // ← CORREGIDO
            default:
                model.addAttribute("error", "Rol desconocido");
                return "empleado-login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/empleado/login";
    }
}