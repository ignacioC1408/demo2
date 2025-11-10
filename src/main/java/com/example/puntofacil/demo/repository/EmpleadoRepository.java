package com.example.puntofacil.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.puntofacil.demo.entity.Empleado;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    // 🔹 Busca un empleado por nombre de usuario
    Optional<Empleado> findByUsername(String username);

    // 🔹 Busca un empleado por token de recuperación
    Optional<Empleado> findByTokenRecuperacion(String token);
}
