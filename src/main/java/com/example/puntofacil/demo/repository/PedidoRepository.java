package com.example.puntofacil.demo.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.puntofacil.demo.entity.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    Optional<Pedido> findByCodigoRetiro(String codigoRetiro);

    List<Pedido> findByEstado(String estado);

    List<Pedido> findByFechaBetween(LocalDate desde, LocalDate hasta);

    @Query("SELECT SUM(p.total) FROM Pedido p WHERE p.fecha BETWEEN :desde AND :hasta")
    BigDecimal sumTotalByFechaBetween(java.time.LocalDate desde, java.time.LocalDate hasta);
}
