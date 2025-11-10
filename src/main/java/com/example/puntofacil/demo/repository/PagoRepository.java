package com.example.puntofacil.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.puntofacil.demo.entity.Pago;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    
    // Buscar pagos por usuario, ordenados por fecha más reciente
    List<Pago> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);
    
    // Buscar pago por preferenceId (para cuando vuelve de Mercado Pago)
    Optional<Pago> findByPreferenceId(String preferenceId);
    
    // Buscar pago por paymentId (ID de transacción de Mercado Pago)
    Optional<Pago> findByPaymentId(String paymentId);
    
    // Buscar pagos por estado
    List<Pago> findByEstado(Pago.EstadoPago estado);
    
    // Buscar pagos pendientes de un usuario
    @Query("SELECT p FROM Pago p WHERE p.usuario.id = :usuarioId AND p.estado = 'PENDIENTE'")
    List<Pago> findPagosPendientesByUsuarioId(@Param("usuarioId") Long usuarioId);
}