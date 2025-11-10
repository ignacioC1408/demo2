package com.example.puntofacil.demo.web;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.puntofacil.demo.service.NotificacionService;

@RestController
@RequestMapping("/test-n8n")
public class N8nTestController {

    private final NotificacionService noti;

    public N8nTestController(NotificacionService noti) {
        this.noti = noti;
    }

    @PostMapping("/pago")
    public void testPago() {
        noti.notificarPagoConfirmado(
                123L, "cliente@ejemplo.com", 25990d,
                List.of(Map.of("codigo","A101","descripcion","Yerba 1kg","qty",2,"precio",4990))
        );
    }

    @PostMapping("/stock")
    public void testStock() {
        noti.notificarLowStock("A101", 3, 5, "Yerba 1kg");
    }
}