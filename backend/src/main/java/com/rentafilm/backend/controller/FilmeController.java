package com.rentafilm.backend.controller;

import com.rentafilm.backend.model.Filme;
import com.rentafilm.backend.repository.FilmeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/filmes")
@Tag(name = "Filmes", description = "Catálogo de filmes para aluguel")
public class FilmeController {

    private final FilmeRepository repository;

    // Injeção de dependência pelo construtor
    public FilmeController(FilmeRepository repository) {
        this.repository = repository;
    }

    // GET /api/filmes -> lista todos os filmes
    @GetMapping
    @Operation(summary = "Lista todos os filmes")
    public List<Filme> listar() {
        return repository.findAll();
    }

    // GET /api/filmes/{id} -> busca um filme; devolve 404 se não existir
    @GetMapping("/{id}")
    @Operation(summary = "Busca um filme pelo id")
    @ApiResponse(responseCode = "404", description = "Filme não encontrado")
    public Filme buscar(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Filme não encontrado"));
    }

    // POST /api/filmes -> cadastra um filme; @Valid devolve 400 se faltar campo obrigatório
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastra um novo filme")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    public Filme criar(@Valid @RequestBody Filme filme) {
        filme.setId(null); // garante que será um INSERT, nunca um UPDATE
        return repository.save(filme);
    }
}