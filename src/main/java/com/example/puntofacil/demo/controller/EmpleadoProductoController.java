package com.example.puntofacil.demo.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.puntofacil.demo.entity.Producto;
import com.example.puntofacil.demo.repository.ProductoRepository;

@Controller
@RequestMapping("/empleado/productos")   // 👈 rutas del panel empleado
public class EmpleadoProductoController {

    private final ProductoRepository productoRepository;

    @Value("${upload.path}")
    private String uploadPath;

    public EmpleadoProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // LISTA DEL PANEL (la tabla azul que ya tenés)
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        return "empleado-productos";   // tu plantilla de listado de productos
    }

    // FORM NUEVO
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("producto", new Producto());
        return "producto-form";
    }

    // FORM EDITAR
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto == null) {
            return "redirect:/empleado/productos";
        }
        model.addAttribute("producto", producto);
        return "producto-form";
    }

    // GUARDAR (NUEVO / EDITAR) + IMAGEN
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto,
                          @RequestParam(name = "imagenFile", required = false) MultipartFile imagenFile) {

        try {
            // Si NO sube imagen nueva y es edición, conservar la anterior
            if ((imagenFile == null || imagenFile.isEmpty()) && producto.getId() != null) {
                productoRepository.findById(producto.getId()).ifPresent(pExistente ->
                        producto.setImagen(pExistente.getImagen())
                );
            }

            // Si sube imagen nueva, guardar archivo y setear nombre
            if (imagenFile != null && !imagenFile.isEmpty()) {
                Path uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
                Files.createDirectories(uploadDir);

                String filename = System.currentTimeMillis() + "_" + imagenFile.getOriginalFilename();
                Path destino = uploadDir.resolve(filename);

                imagenFile.transferTo(destino.toFile());

                producto.setImagen(filename);
            }
        } catch (IOException e) {
            e.printStackTrace(); // si falla la imagen, igual seguimos guardando el producto
        }

        productoRepository.save(producto);
        return "redirect:/empleado/productos";
    }

    // ELIMINAR
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        productoRepository.deleteById(id);
        return "redirect:/empleado/productos";
    }
}
