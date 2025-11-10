package com.example.puntofacil.demo.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.puntofacil.demo.integraciones.N8nClient;

@Service
public class NotificacionService {

    private final N8nClient n8n;

    public NotificacionService(N8nClient n8n) {
        this.n8n = n8n;
    }

    // Llamar cuando se confirme el pago
    public void notificarPagoConfirmado(Long orderId, String email, double total, List<Map<String, Object>> items) {
        Map<String, Object> body = Map.of(
                "event", "payment_confirmed",
                "orderId", orderId,
                "email", email,
                "total", total,
                "items", items
        );
        n8n.notificarPago(body);
    }

    // Llamar cuando el stock esté bajo
    public void notificarLowStock(String codigo, int stockActual, int stockMinimo, String descripcion) {
        Map<String, Object> body = Map.of(
                "event", "low_stock",
                "codigo", codigo,
                "stockActual", stockActual,
                "stockMinimo", stockMinimo,
                "descripcion", descripcion
        );
        n8n.notificarLowStock(body);
    }
}