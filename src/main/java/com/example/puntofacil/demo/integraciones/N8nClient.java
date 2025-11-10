package com.example.puntofacil.demo.integraciones;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class N8nClient {

    private final RestTemplate rest = new RestTemplate();

    @Value("${n8n.webhook.pago-url}")  private String pagoUrl;
    @Value("${n8n.webhook.stock-url}") private String stockUrl;
    @Value("${n8n.api-key}")           private String apiKey;

    public void notificarPago(Map<String, Object> payload) {
        enviar(pagoUrl, payload);
    }

    public void notificarLowStock(Map<String, Object> payload) {
        enviar(stockUrl, payload);
    }

    private void enviar(String url, Map<String, Object> payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);   // Content-Type: application/json
        headers.set("x-api-key", apiKey);                     // Header Auth para n8n

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
        rest.postForEntity(url, entity, Void.class);
    }
}
