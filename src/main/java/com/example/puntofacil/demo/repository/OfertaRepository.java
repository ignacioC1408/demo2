package com.example.puntofacil.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.puntofacil.demo.entity.Oferta;

public interface OfertaRepository extends JpaRepository<Oferta, Long> {
}
