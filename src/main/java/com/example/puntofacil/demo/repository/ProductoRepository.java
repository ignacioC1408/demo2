package com.example.puntofacil.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.puntofacil.demo.entity.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Buscar por código de producto (por si en algún momento lo necesitas)
    Optional<Producto> findByCodigoProducto(Integer codigoProducto);

    // Verificar si existe un producto con determinado código
    boolean existsByCodigoProducto(Integer codigoProducto);
}
