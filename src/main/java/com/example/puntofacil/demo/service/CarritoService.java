package com.example.puntofacil.demo.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.puntofacil.demo.entity.Producto;
import com.example.puntofacil.demo.repository.ProductoRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class CarritoService {

    private final ProductoRepository productoRepository;

    public CarritoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // Estructura del carrito guardado en sesión
    @SuppressWarnings("unchecked")
    private Map<Integer, Map<String, Object>> getCarrito(HttpSession session) {
        Map<Integer, Map<String, Object>> carrito = 
            (Map<Integer, Map<String, Object>>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new HashMap<>();
            session.setAttribute("carrito", carrito);
        }
        return carrito;
    }

    // Agregar producto
    public void agregarProducto(Integer codigoProducto, int cantidad, HttpSession session) {
        Map<Integer, Map<String, Object>> carrito = getCarrito(session);
        Optional<Producto> optProducto = productoRepository.findByCodigoProducto(codigoProducto);

        if (optProducto.isEmpty()) {
            throw new IllegalArgumentException("Producto no encontrado");
        }

        Producto producto = optProducto.get();

        Map<String, Object> item = carrito.getOrDefault(codigoProducto, new HashMap<>());
        int cantidadActual = (int) item.getOrDefault("cantidad", 0);
        cantidad += cantidadActual;

        item.put("producto", producto);
        item.put("cantidad", cantidad);
        carrito.put(codigoProducto, item);

        session.setAttribute("carrito", carrito);
    }

    // Modificar cantidad (+ / -)
    public void modificarCantidad(Integer codigoProducto, String accion, HttpSession session) {
        Map<Integer, Map<String, Object>> carrito = getCarrito(session);
        Map<String, Object> item = carrito.get(codigoProducto);

        if (item == null) return;

        int cantidad = (int) item.get("cantidad");
        if ("incrementar".equals(accion)) cantidad++;
        else if ("decrementar".equals(accion)) cantidad--;

        if (cantidad <= 0) carrito.remove(codigoProducto);
        else item.put("cantidad", cantidad);

        session.setAttribute("carrito", carrito);
    }

    // Eliminar producto
    public void eliminarProducto(Integer codigoProducto, HttpSession session) {
        Map<Integer, Map<String, Object>> carrito = getCarrito(session);
        carrito.remove(codigoProducto);
        session.setAttribute("carrito", carrito);
    }

    // Vaciar carrito
    public void vaciarCarrito(HttpSession session) {
        session.removeAttribute("carrito");
    }

    // Calcular totales del carrito
    public Map<String, Object> calcularTotales(HttpSession session) {
        Map<Integer, Map<String, Object>> carrito = getCarrito(session);

        BigDecimal subtotal = BigDecimal.ZERO;
        List<Map<String, Object>> items = new ArrayList<>();

        for (var entry : carrito.values()) {
            Producto p = (Producto) entry.get("producto");
            int cantidad = (int) entry.get("cantidad");
            BigDecimal subtotalItem = p.getPrecioBase().multiply(BigDecimal.valueOf(cantidad));

            Map<String, Object> item = new HashMap<>();
            item.put("producto", p);
            item.put("cantidad", cantidad);
            item.put("subtotal", subtotalItem);
            items.add(item);

            subtotal = subtotal.add(subtotalItem);
        }

        BigDecimal envio = subtotal.compareTo(BigDecimal.valueOf(5000)) >= 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(500);
        BigDecimal total = subtotal.add(envio);

        Map<String, Object> resumen = new HashMap<>();
        resumen.put("items", items);
        resumen.put("subtotal", subtotal);
        resumen.put("total", total);

        return resumen;
    }
}
