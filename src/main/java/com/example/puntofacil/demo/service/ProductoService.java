package com.example.puntofacil.demo.service;

import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.puntofacil.demo.entity.Producto;
import com.example.puntofacil.demo.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository repo;
    private final Random rnd = new Random();

    public ProductoService(ProductoRepository repo) {
        this.repo = repo;
    }

    public List<Producto> listarTodos() {
        return repo.findAll();
    }

    @Transactional
    public Producto crearProducto(Producto p) {
        if (p.getCodigoProducto() == null) {
            p.setCodigoProducto(generarCodigoUnico());
        } else {
            if (repo.existsByCodigoProducto(p.getCodigoProducto())) {
                throw new IllegalArgumentException("Código de producto ya existe");
            }
        }
        return repo.save(p);
    }

    private Integer generarCodigoUnico() {
        int codigo;
        int intentos = 0;
        do {
            codigo = rnd.nextInt(9999) + 1; // 1..9999
            intentos++;
            if (intentos > 20000) {
                throw new RuntimeException("No se pudo generar código único");
            }
        } while (repo.existsByCodigoProducto(codigo));
        return codigo;
    }
}
