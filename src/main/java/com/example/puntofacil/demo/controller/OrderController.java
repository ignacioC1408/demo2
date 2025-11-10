package com.example.puntofacil.demo.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.puntofacil.demo.entity.DetallePedido;
import com.example.puntofacil.demo.entity.Pedido;
import com.example.puntofacil.demo.entity.Producto;
import com.example.puntofacil.demo.entity.Usuario;
import com.example.puntofacil.demo.repository.PedidoRepository;
import com.example.puntofacil.demo.repository.ProductoRepository;
import com.example.puntofacil.demo.repository.UsuarioRepository;
import com.example.puntofacil.demo.util.Cart;
import com.example.puntofacil.demo.util.CartItem;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usuario")
public class OrderController {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository; // para cargar usuario logueado

    public OrderController(PedidoRepository pedidoRepository,
                           ProductoRepository productoRepository,
                           UsuarioRepository usuarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/checkout")
    public String checkoutForm(HttpSession session, Model model) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            model.addAttribute("error", "El carrito está vacío.");
            return "redirect:/usuario/productos";
        }
        model.addAttribute("cart", cart);
        return "usuario-checkout";
    }

    @PostMapping("/checkout")
    public String doCheckout(HttpSession session, Model model) {
        // obtener carrito
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            model.addAttribute("error", "El carrito está vacío.");
            return "redirect:/usuario/productos";
        }

        // usuario logueado en session: SE RECOMIENDA guardar el objeto Usuario en session al login.
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            model.addAttribute("error", "Debes iniciar sesión para finalizar la compra.");
            return "redirect:/usuario/login";
        }

        // validar stock y construir detalles
        List<DetallePedido> detalles = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : cart.getItems()) {
            Optional<Producto> optProd = productoRepository.findById(ci.getProductoId());
            if (optProd.isEmpty()) {
                model.addAttribute("error", "Producto no existe: " + ci.getNombre());
                return "redirect:/usuario/cart/view";
            }
            Producto prod = optProd.get();

            // verificar stock
            if (prod.getStock() != null && prod.getStock().compareTo(ci.getCantidad()) < 0) {
                model.addAttribute("error", "Stock insuficiente para: " + prod.getNombre());
                return "redirect:/usuario/cart/view";
            }

            DetallePedido detalle = new DetallePedido();
            detalle.setProducto(prod);
            detalle.setCantidadUnidad(ci.getCantidad());
            detalle.setPrecioUnitario(ci.getPrecioUnitario());
            detalles.add(detalle);

            total = total.add(ci.getPrecioUnitario().multiply(ci.getCantidad()));
        }

        // crear pedido
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuarioRepository.findById(usuario.getId()).orElse(null));
        pedido.setEstado("PENDIENTE");
        pedido.setTotal(total);
        String codigo = generateUniqueCodigoRetiro();
        pedido.setCodigoRetiro(codigo);
        // generar URL de QR (Google Chart) — simple y público
        String qrUrl = "https://chart.googleapis.com/chart?chs=200x200&cht=qr&chl=" + codigo;
        pedido.setQrCodeUrl(qrUrl);

        // persistir pedido y detalles (cascade)
        pedidoRepository.save(pedido);

        // ahora se debe enlazar detalles con el pedido y restar stock
        for (DetallePedido d : detalles) {
            d.setPedido(pedido);
            // restar stock en producto
            Producto prod = d.getProducto();
            BigDecimal newStock = prod.getStock().subtract(d.getCantidadUnidad());
            prod.setStock(newStock.max(BigDecimal.ZERO));
            productoRepository.save(prod);
        }

        // guardar detalles manualmente (si tenés DetallePedidoRepository se usará)
        pedido.setDetalles(detalles);
        pedidoRepository.save(pedido); // guarda también detalles si cascade = ALL en entidad (si no, usar repo detalle)

        // limpiar carrito
        cart.clear();
        session.setAttribute("cart", cart);

        // pasar datos a la vista de éxito
        model.addAttribute("codigoRetiro", pedido.getCodigoRetiro());
        model.addAttribute("qrUrl", pedido.getQrCodeUrl());
        model.addAttribute("total", pedido.getTotal().setScale(2));
        return "usuario-order-success";
    }

    private String generateUniqueCodigoRetiro() {
        // generar un código corto único (6 chars alfanuméricos) y verificar en DB
        String codigo;
        do {
            codigo = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        } while (pedidoRepository.findByCodigoRetiro(codigo).isPresent());
        return codigo;
    }
}
