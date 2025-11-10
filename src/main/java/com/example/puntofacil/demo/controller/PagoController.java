package com.example.puntofacil.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usuario/pago")
public class PagoController {

    @PostMapping("/procesar")
    public String procesarPago(HttpSession session) {
        System.out.println("✅ PagoController: Forward a procesar-pago");
        // Usa forward para mantener el método POST
        return "forward:/usuario/carrito/procesar-pago";
    }

    // ⚠️ Antes era @GetMapping("/exitoso")
    // Eso chocaba con UsuarioPagoController#pagoExitoso (misma ruta)
    // Ahora esta ruta queda solo para pruebas locales
    @GetMapping("/exitoso-local")
    public String pagoExitoso() {
        return "pago-exitoso";
    }

    // ⚠️ Antes: @GetMapping("/fallido")
    @GetMapping("/fallido-local")
    public String pagoFallido() {
        return "pago-fallido";
    }

    // ⚠️ Antes: @GetMapping("/pendiente")
    @GetMapping("/pendiente-local")
    public String pagoPendiente() {
        return "pago-pendiente";
    }
}
