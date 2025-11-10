package com.example.puntofacil.demo.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.puntofacil.demo.entity.Producto;
import com.example.puntofacil.demo.repository.ProductoRepository;

@Component
public class DataLoader implements CommandLineRunner {

    private final ProductoRepository productoRepository;

    public DataLoader(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (productoRepository.count() == 0) {
            List<Producto> inicial = List.of(
                new Producto(101, "Carne Picada", null, new BigDecimal("2500.00"), "KG", new BigDecimal("15.000")),
                new Producto(102, "Milanesa de Pollo", null, new BigDecimal("3000.00"), "KG", new BigDecimal("10.000")),
                new Producto(201, "Papa Blanca", null, new BigDecimal("800.00"), "KG", new BigDecimal("50.000")),
                new Producto(301, "Lavandina", null, new BigDecimal("500.00"), "LT", new BigDecimal("20.000")),
                new Producto(401, "Agua Mineral 2L", null, new BigDecimal("700.00"), "UNIDAD", new BigDecimal("50.000"))
            );
            productoRepository.saveAll(inicial);
            System.out.println("Cargados productos de ejemplo: " + inicial.size());
        }
    }
}
