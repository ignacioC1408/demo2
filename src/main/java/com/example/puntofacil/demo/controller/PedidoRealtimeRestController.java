package com.example.puntofacil.demo.controller;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.puntofacil.demo.entity.Pedido;
import com.example.puntofacil.demo.repository.PedidoRepository;

@RestController
@RequestMapping("/empleado/panel/pedidos/api")
public class PedidoRealtimeRestController {

    private final PedidoRepository pedidoRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public PedidoRealtimeRestController(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @GetMapping
    public List<PedidoDto> listarPedidos(@RequestParam(value = "estado", required = false) String estado) {
        List<Pedido> pedidos;

        if (estado != null && !estado.isBlank()) {
            pedidos = pedidoRepository.findByEstado(estado);
        } else {
            pedidos = pedidoRepository.findAll();
        }

        return pedidos.stream()
                .map(p -> new PedidoDto(
                        p.getId(),
                        p.getUsuario() != null ? p.getUsuario().getNombre() : "Sin usuario",
                        p.getEstado(),
                        p.getTotal(),
                        p.getCodigoRetiro(),
                        p.getFecha() != null ? p.getFecha().format(formatter) : ""
                ))
                .collect(Collectors.toList());
    }

    // DTO simple para evitar problemas de serialización con relaciones
    public static class PedidoDto {
        private Long id;
        private String usuario;
        private String estado;
        private BigDecimal total;
        private String codigoRetiro;
        private String fecha;

        public PedidoDto(Long id, String usuario, String estado,
                         BigDecimal total, String codigoRetiro, String fecha) {
            this.id = id;
            this.usuario = usuario;
            this.estado = estado;
            this.total = total;
            this.codigoRetiro = codigoRetiro;
            this.fecha = fecha;
        }

        public Long getId() { return id; }
        public String getUsuario() { return usuario; }
        public String getEstado() { return estado; }
        public BigDecimal getTotal() { return total; }
        public String getCodigoRetiro() { return codigoRetiro; }
        public String getFecha() { return fecha; }
    }
}
