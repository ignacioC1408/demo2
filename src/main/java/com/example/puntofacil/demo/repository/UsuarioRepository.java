package com.example.puntofacil.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.puntofacil.demo.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // ✅ NUEVO: Buscar por username (necesario para el login)
    Optional<Usuario> findByUsername(String username);

    // Buscar por email
    Optional<Usuario> findByEmail(String email);

    // Buscar por nombre
    Optional<Usuario> findByNombre(String nombre);
}