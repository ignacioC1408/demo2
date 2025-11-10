package com.example.puntofacil.demo.web;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.puntofacil.demo.service.NotificacionService;

@Controller
public class UsuarioPagoController {

    private final NotificacionService noti;

    public UsuarioPagoController(NotificacionService noti) {
        this.noti = noti;
    }

    @GetMapping("/usuario/pago/exitoso")
    public String pagoExitoso(
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) String email
    ) {
        // 1) Tu lógica existente...

        // 2) TIPAR EXPLÍCITAMENTE:
        List<Map<String, Object>> items = List.of(
            Map.<String, Object>of(
                "codigo", "A101",
                "descripcion", "Yerba 1kg",
                "qty", Integer.valueOf(2),
                "precio", Integer.valueOf(4990)
            )
        );

        double total = 25990d;

        // 4) Notificar a n8n
        noti.notificarPagoConfirmado(orderId, email, total, items);

        return "pago_exitoso";
    }
}
