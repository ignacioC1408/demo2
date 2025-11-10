package com.example.puntofacil.demo.controller;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.puntofacil.demo.entity.Producto;
import com.example.puntofacil.demo.repository.ProductoRepository;
import com.example.puntofacil.demo.util.Cart;
import com.example.puntofacil.demo.util.CartItem;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usuario/cart")
public class CartController {

    private final ProductoRepository productoRepository;

    public CartController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    private Cart getCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productoId,
                            @RequestParam String cantidad, // string for decimal parsing
                            HttpSession session, Model model) {
        Optional<Producto> opt = productoRepository.findById(productoId);
        if (opt.isEmpty()) {
            model.addAttribute("error", "Producto no encontrado");
            return "redirect:/usuario/productos";
        }

        Producto p = opt.get();
        BigDecimal qty;
        try {
            qty = new BigDecimal(cantidad);
        } catch (Exception e) {
            qty = BigDecimal.ONE;
        }
        // redondeamos a 3 decimales para evitar precisión extra
        qty = qty.setScale(3, BigDecimal.ROUND_HALF_UP);

        // verificar stock
        if (p.getStock() != null && p.getStock().compareTo(qty) < 0) {
            model.addAttribute("error", "Stock insuficiente para " + p.getNombre());
            return "redirect:/usuario/productos";
        }

        CartItem item = new CartItem(p.getId(), p.getNombre(), p.getUnidadMedida(), p.getPrecioBase(), qty);
        Cart cart = getCart(session);
        cart.add(item);

        return "redirect:/usuario/cart/view";
    }

    @GetMapping("/view")
    public String viewCart(HttpSession session, Model model) {
        Cart cart = getCart(session);
        model.addAttribute("cart", cart);
        return "usuario-cart";
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam Long productoId,
                                 @RequestParam String cantidad,
                                 HttpSession session) {
        Cart cart = getCart(session);
        BigDecimal qty = new BigDecimal(cantidad).setScale(3, BigDecimal.ROUND_HALF_UP);
        cart.updateQuantity(productoId, qty);
        return "redirect:/usuario/cart/view";
    }

    @PostMapping("/remove")
    public String remove(@RequestParam Long productoId, HttpSession session) {
        Cart cart = getCart(session);
        cart.remove(productoId);
        return "redirect:/usuario/cart/view";
    }

    @PostMapping("/clear")
    public String clear(HttpSession session) {
        Cart cart = getCart(session);
        cart.clear();
        return "redirect:/usuario/productos";
    }
}
