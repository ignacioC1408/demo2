package com.example.puntofacil.demo.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;

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

import com.example.puntofacil.demo.entity.Oferta;
import com.example.puntofacil.demo.entity.Producto;
import com.example.puntofacil.demo.repository.OfertaRepository;
import com.example.puntofacil.demo.repository.ProductoRepository;

@Controller
@RequestMapping("/empleado/ofertas")
public class OfertaController {

    private final OfertaRepository ofertaRepository;
    private final ProductoRepository productoRepository;

    @Value("${upload.path}")
    private String uploadPath;

    public OfertaController(OfertaRepository ofertaRepository,
                            ProductoRepository productoRepository) {
        this.ofertaRepository = ofertaRepository;
        this.productoRepository = productoRepository;
    }

    // LISTAR OFERTAS
    @GetMapping
    public String listar(Model model) {
        List<Oferta> ofertas = ofertaRepository.findAll();
        model.addAttribute("ofertas", ofertas);
        return "ofertas-list";
    }

    // NUEVA OFERTA
    @GetMapping("/nueva")
    public String nueva(Model model) {
        Oferta oferta = new Oferta();
        oferta.setFechaInicio(LocalDate.now()); // por defecto hoy
        model.addAttribute("oferta", oferta);
        model.addAttribute("productos", productoRepository.findAll());
        return "oferta-form";
    }

    // EDITAR OFERTA
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Oferta oferta = ofertaRepository.findById(id).orElse(null);
        if (oferta == null) {
            return "redirect:/empleado/ofertas";
        }
        model.addAttribute("oferta", oferta);
        model.addAttribute("productos", productoRepository.findAll());
        return "oferta-form";
    }

    // GUARDAR / ACTUALIZAR OFERTA
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Oferta oferta,
                          @RequestParam("imagenFile") MultipartFile imagenFile) throws IOException {

        // 🔹 Si trae producto.id desde el form, lo resolvemos bien desde la base
        if (oferta.getProducto() != null && oferta.getProducto().getId() != null) {
            Producto prod = productoRepository.findById(oferta.getProducto().getId())
                    .orElse(null);
            oferta.setProducto(prod);
        }

        // 🔹 Imagen: si no sube nueva y está editando, conservar la anterior
        if (oferta.getId() != null && (imagenFile == null || imagenFile.isEmpty())) {
            ofertaRepository.findById(oferta.getId())
                    .ifPresent(oAnt -> oferta.setImagen(oAnt.getImagen()));
        }

        // 🔹 Si viene archivo nuevo, lo guardamos en ./uploads
        if (imagenFile != null && !imagenFile.isEmpty()) {
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String fileName = System.currentTimeMillis() + "_" + imagenFile.getOriginalFilename();
            Path destino = uploadDir.resolve(fileName);
            Files.copy(imagenFile.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            oferta.setImagen(fileName);
        }

        // 🔹 Si tiene producto y porcentaje, calculamos precioOferta automáticamente
        if (oferta.getProducto() != null &&
            oferta.getProducto().getPrecioBase() != null &&
            oferta.getPorcentaje() != null) {

            BigDecimal cien = new BigDecimal("100");
            BigDecimal desc = oferta.getProducto().getPrecioBase()
                    .multiply(oferta.getPorcentaje())
                    .divide(cien, 2, RoundingMode.HALF_UP);

            oferta.setPrecioOferta(oferta.getProducto().getPrecioBase().subtract(desc));
        }

        // 🔹 Si no puso fecha de inicio, por defecto hoy
        if (oferta.getFechaInicio() == null) {
            oferta.setFechaInicio(LocalDate.now());
        }

        ofertaRepository.save(oferta);
        return "redirect:/empleado/ofertas";
    }

    // ELIMINAR OFERTA
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        ofertaRepository.deleteById(id);
        return "redirect:/empleado/ofertas";
    }
}
