package com.example.puntofacil.demo.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.puntofacil.demo.entity.Producto;
import com.example.puntofacil.demo.repository.ProductoRepository;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePayerRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usuario/carrito")
public class CarritoController {

    private final ProductoRepository productoRepository;

    // Claves de Mercado Pago tomadas del application.yml
    @Value("${mercadopago.access-token}")
    private String mercadoPagoToken;

    @Value("${mercadopago.success-url}")
    private String successUrl;

    @Value("${mercadopago.failure-url}")
    private String failureUrl;

    @Value("${mercadopago.pending-url}")
    private String pendingUrl;

    @PostConstruct
    public void initMercadoPago() {
        MercadoPagoConfig.setAccessToken(mercadoPagoToken);
    }

    public CarritoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // Ver el carrito
    @GetMapping("/ver")
    public String verCarrito(HttpSession session, Model model) {
        Map<Long, CarritoItem> carrito = obtenerCarrito(session);
        model.addAttribute("carrito", carrito);

        BigDecimal total = carrito.values().stream()
                .map(CarritoItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("total", total);

        return "carrito-ver";
    }

    // Agregar producto al carrito
    @PostMapping("/agregar")
    public String agregarAlCarrito(@RequestParam("idProducto") Long idProducto,
                                   @RequestParam(defaultValue = "1") int cantidad,
                                   HttpSession session) {

        Map<Long, CarritoItem> carrito = obtenerCarrito(session);

        productoRepository.findById(idProducto).ifPresent(producto -> {
            CarritoItem item = carrito.getOrDefault(idProducto, new CarritoItem(producto, BigDecimal.ZERO));
            item.setCantidad(item.getCantidad().add(BigDecimal.valueOf(cantidad)));
            carrito.put(idProducto, item);
        });

        session.setAttribute("carrito", carrito);
        return "redirect:/usuario/carrito/ver";
    }

    // Actualizar cantidad
    @PostMapping("/actualizar-cantidad")
    public String actualizarCantidad(@RequestParam("idProducto") Long idProducto,
                                     @RequestParam("cantidad") BigDecimal cantidad,
                                     HttpSession session) {

        Map<Long, CarritoItem> carrito = obtenerCarrito(session);
        CarritoItem item = carrito.get(idProducto);

        if (item != null && cantidad.compareTo(BigDecimal.ZERO) > 0) {
            item.setCantidad(cantidad);
        } else {
            carrito.remove(idProducto);
        }

        session.setAttribute("carrito", carrito);
        return "redirect:/usuario/carrito/ver";
    }

    // Eliminar producto
    @PostMapping("/eliminar")
    public String eliminarProducto(@RequestParam("idProducto") Long idProducto,
                                   HttpSession session) {
        Map<Long, CarritoItem> carrito = obtenerCarrito(session);
        carrito.remove(idProducto);
        session.setAttribute("carrito", carrito);
        return "redirect:/usuario/carrito/ver";
    }

    // Vaciar carrito
    @PostMapping("/vaciar")
    public String vaciarCarrito(HttpSession session) {
        session.removeAttribute("carrito");
        return "redirect:/usuario/carrito/ver";
    }

    // Procesar pago con Mercado Pago - CORREGIDO
    @PostMapping("/procesar-pago")
    public String procesarPago(HttpSession session) {
        try {
            System.out.println("=== INICIANDO PROCESO DE PAGO ===");
            
            Map<Long, CarritoItem> carrito = obtenerCarrito(session);
            System.out.println("Items en carrito: " + carrito.size());

            if (carrito.isEmpty()) {
                return "redirect:/usuario/carrito/ver?error=El carrito está vacío.";
            }

            List<PreferenceItemRequest> items = new ArrayList<>();

            for (CarritoItem item : carrito.values()) {
                System.out.println("Producto: " + item.getProducto().getNombre() + 
                                 " | Cantidad: " + item.getCantidad() + 
                                 " | Precio: " + item.getProducto().getPrecioBase());
                
                // Validar que el precio sea válido
                if (item.getProducto().getPrecioBase().compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("ERROR: Precio inválido para " + item.getProducto().getNombre());
                    return "redirect:/usuario/carrito/ver?error=Precio inválido para " + item.getProducto().getNombre();
                }
                
                items.add(
                    PreferenceItemRequest.builder()
                        .title(item.getProducto().getNombre())
                        .quantity(item.getCantidad().intValueExact())
                        .unitPrice(item.getProducto().getPrecioBase())
                        .currencyId("ARS")
                        .build()
                );
            }

            PreferencePayerRequest payer = PreferencePayerRequest.builder()
                    .email("cliente@puntofacil.com")
                    .build();

            // ✅ PREFERENCE REQUEST CORREGIDO - sin autoReturn que causaba el error
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .payer(payer)
                    .backUrls(
                        com.mercadopago.client.preference.PreferenceBackUrlsRequest.builder()
                            .success(successUrl)
                            .failure(failureUrl)
                            .pending(pendingUrl)
                            .build()
                    )
                    // .autoReturn("approved") // ← ELIMINADO PARA EVITAR EL ERROR
                    .build();

            System.out.println("Creando preferencia en Mercado Pago...");
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);
            
            System.out.println("Preferencia creada exitosamente: " + preference.getId());
            session.removeAttribute("carrito");
            return "redirect:" + preference.getInitPoint();

        } catch (com.mercadopago.exceptions.MPApiException apiException) {
            System.out.println("=== ERROR DE API MERCADO PAGO ===");
            System.out.println("Status Code: " + apiException.getStatusCode());
            if (apiException.getApiResponse() != null) {
                System.out.println("Response Content: " + apiException.getApiResponse().getContent());
            }
            System.out.println("Error Message: " + apiException.getMessage());
            
            return "redirect:/usuario/carrito/ver?error=Error en Mercado Pago: " + apiException.getMessage();

        } catch (Exception e) {
            System.out.println("=== ERROR GENERAL ===");
            e.printStackTrace();
            return "redirect:/usuario/carrito/ver?error=Error al procesar el pago: " + e.getMessage();
        }
    }

    // Método auxiliar - asegura que nunca devuelva null
    @SuppressWarnings("unchecked")
    private Map<Long, CarritoItem> obtenerCarrito(HttpSession session) {
        Map<Long, CarritoItem> carrito = (Map<Long, CarritoItem>) session.getAttribute("carrito");
        return carrito != null ? carrito : new HashMap<>();
    }

    // Clase interna para manejar los ítems del carrito
    private static class CarritoItem {
        private Producto producto;
        private BigDecimal cantidad;

        public CarritoItem(Producto producto, BigDecimal cantidad) {
            this.producto = producto;
            this.cantidad = cantidad;
        }

        public Producto getProducto() {
            return producto;
        }

        public BigDecimal getCantidad() {
            return cantidad;
        }

        public void setCantidad(BigDecimal cantidad) {
            this.cantidad = cantidad;
        }

        public BigDecimal getTotal() {
            return producto.getPrecioBase().multiply(cantidad);
        }

        // Getters directos para Thymeleaf
        public String getNombre() {
            return producto.getNombre();
        }

        public BigDecimal getPrecio() {
            return producto.getPrecioBase();
        }
    }
}