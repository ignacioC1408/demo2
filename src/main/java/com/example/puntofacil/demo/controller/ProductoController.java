package com.example.puntofacil.demo.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.puntofacil.demo.entity.Producto;
import com.example.puntofacil.demo.repository.ProductoRepository;

@Controller
@RequestMapping("/usuario/productos") // ✅ RUTA PARA USUARIOS
public class ProductoController {

    private final ProductoRepository productoRepository;

    public ProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @GetMapping("/lista")
    public String listarProductos(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        return "producto-lista"; // Template para usuarios
    }

    // Endpoint para devolver el step por unidad (usado en carrito)
    @GetMapping("/{id}/step")
    @ResponseBody
    public String getStep(@PathVariable Long id) {
        Optional<Producto> opt = productoRepository.findById(id);
        if (opt.isPresent()) {
            Producto p = opt.get();
            String um = p.getUnidadMedida();
            if ("KG".equalsIgnoreCase(um)) return "0.1";
            if ("UNIDAD".equalsIgnoreCase(um)) return "1";
            if ("LT".equalsIgnoreCase(um)) return "0.1";
            return "1";
        }
        return "1";
    }
}