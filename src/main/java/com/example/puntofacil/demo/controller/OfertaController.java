package com.example.puntofacil.demo.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    @Value("${upload.path:./uploads}")
    private String uploadPath;

    public OfertaController(OfertaRepository ofertaRepository,
                            ProductoRepository productoRepository) {
        this.ofertaRepository = ofertaRepository;
        this.productoRepository = productoRepository;
    }

    // Listado de ofertas
    @GetMapping
    public String listar(Model model) {
        List<Oferta> ofertas = ofertaRepository.findAll();
        model.addAttribute("ofertas", ofertas);
        return "ofertas-list";
    }

    // Formulario nueva oferta
    @GetMapping("/nuevo")
    public String nuevaOferta(Model model) {
        model.addAttribute("oferta", new Oferta());
        model.addAttribute("productos", productoRepository.findAll());
        return "oferta-form";
    }

    // Guardar oferta
    @PostMapping("/guardar")
    public String guardarOferta(@ModelAttribute Oferta oferta,
                                @RequestParam("imagenFile") MultipartFile imagenFile) throws IOException {

        // 1️⃣ Recuperar el producto desde la BD
        Producto prod = null;
        if (oferta.getProducto() != null && oferta.getProducto().getId() != null) {
            prod = productoRepository.findById(oferta.getProducto().getId()).orElse(null);
        }

        // 2️⃣ Calcular precio de oferta usando porcentaje
        if (prod != null && prod.getPrecioBase() != null && oferta.getPorcentaje() != null) {
            BigDecimal cien = BigDecimal.valueOf(100);
            BigDecimal descuento = cien.subtract(BigDecimal.valueOf(oferta.getPorcentaje()));
            BigDecimal precioCalculado = prod.getPrecioBase()
                    .multiply(descuento)
                    .divide(cien, 2, RoundingMode.HALF_UP);

            oferta.setProducto(prod);
            oferta.setPrecioOferta(precioCalculado);
        } else if (prod != null && prod.getPrecioBase() != null) {
            // Fallback: sin porcentaje, usar precio base
            oferta.setProducto(prod);
            oferta.setPrecioOferta(prod.getPrecioBase());
        }

        // 3️⃣ Guardar imagen si se subió
        if (imagenFile != null && !imagenFile.isEmpty()) {
            Path directorio = Paths.get(uploadPath).toAbsolutePath().normalize();
            Files.createDirectories(directorio);

            String nombreArchivo = System.currentTimeMillis() + "_" + imagenFile.getOriginalFilename();
            Path rutaArchivo = directorio.resolve(nombreArchivo);

            imagenFile.transferTo(rutaArchivo.toFile());
            oferta.setImagen(nombreArchivo);
        }

        ofertaRepository.save(oferta);
        return "redirect:/empleado/ofertas";
    }
}

