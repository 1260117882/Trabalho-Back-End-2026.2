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

// Controller REST: recebe as requisições HTTP em /api/filmes e devolve JSON.
// Aqui ficam as rotas de leitura (GET) e criação (POST) do catálogo de filmes.
@RestController
@RequestMapping("/api/filmes")
// Agrupa as rotas na documentação OpenAPI/Swagger sob o nome "Filmes".
@Tag(name = "Filmes", description = "Catálogo de filmes para aluguel")
public class FilmeController {

    // Repositório (Spring Data JPA): faz a comunicação com a tabela "filmes" no PostgreSQL.
    private final FilmeRepository repository;

    // Injeção de dependência pelo construtor: o Spring entrega o repositório pronto.
    public FilmeController(FilmeRepository repository) {
        this.repository = repository;
    }

    // GET /api/filmes -> lista todos os filmes (retorna 200 OK).
    @GetMapping
    @Operation(summary = "Lista todos os filmes")
    public List<Filme> listar() {
        return repository.findAll();
    }

    // GET /api/filmes/{id} -> busca um filme pelo id.
    // Se não existir, lança exceção e o Spring responde 404 Not Found.
    @GetMapping("/{id}")
    @Operation(summary = "Busca um filme pelo id")
    @ApiResponse(responseCode = "404", description = "Filme não encontrado")
    public Filme buscar(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Filme não encontrado"));
    }

    // POST /api/filmes -> cadastra um filme e responde 201 Created.
    // @Valid confere as regras da entidade (título obrigatório, preço > 0);
    // se alguma falhar, o Spring responde 400 Bad Request sem chegar ao banco.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastra um novo filme")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    public Filme criar(@Valid @RequestBody Filme filme) {
        filme.setId(null); // garante que será um INSERT, nunca um UPDATE
        return repository.save(filme); // grava no banco e devolve o filme com o id gerado
    }
}
