package com.example.puntofacil.demo.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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
@RequestMapping("/empleado/productos")
public class EmpleadoProductoController {

    private final ProductoRepository productoRepository;

    @Value("${upload.path}")
    private String uploadPath;   // ./uploads (definido en application.properties)

    public EmpleadoProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // LISTA
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        return "producto-list";  // tu tabla linda de productos
    }

    // NUEVO
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("producto", new Producto());
        return "producto-form";
    }

    // EDITAR
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Producto p = productoRepository.findById(id).orElse(null);
        if (p == null) return "redirect:/empleado/productos";
        model.addAttribute("producto", p);
        return "producto-form";
    }

    // GUARDAR (nuevo o edición)
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto,
                          @RequestParam("imagenFile") MultipartFile imagenFile) throws IOException {

        // Si estoy editando, conservar la imagen si no se sube una nueva
        if ((producto.getId() != null) && (imagenFile == null || imagenFile.isEmpty())) {
            productoRepository.findById(producto.getId())
                    .ifPresent(pAnt -> producto.setImagen(pAnt.getImagen()));
        }

        // Si viene una imagen nueva, guardarla en /uploads
        if (imagenFile != null && !imagenFile.isEmpty()) {
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String fileName = System.currentTimeMillis() + "_" + imagenFile.getOriginalFilename();
            Path destino = uploadDir.resolve(fileName);
            Files.copy(imagenFile.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

            producto.setImagen(fileName);
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
