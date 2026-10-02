package com.rentafilm.backend.repository;

import com.rentafilm.backend.model.Filme;
import org.springframework.data.jpa.repository.JpaRepository;

// O Spring Data gera sozinho findAll, findById, save, deleteById etc.
public interface FilmeRepository extends JpaRepository<Filme, Long> {
}