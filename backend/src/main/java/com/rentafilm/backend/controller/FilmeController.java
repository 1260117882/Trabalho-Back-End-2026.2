package com.rentafilm.backend.controller;

import com.rentafilm.backend.exception.ErroResposta;
import com.rentafilm.backend.model.Filme;
import com.rentafilm.backend.repository.FilmeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

// Controller REST: recebe as requisições HTTP em /api/filmes e devolve JSON.
// Rotas de leitura (GET) e criação (POST) do catálogo + atualização (PUT) e exclusão (DELETE).
@RestController
@RequestMapping("/api/filmes")
// Agrupa as rotas na documentação OpenAPI/Swagger sob o nome "Filmes".
@Tag(name = "Filmes", description = "Catálogo de filmes para aluguel")
public class FilmeController {

    // Exemplos de corpo de erro mostrados no Swagger (iguais ao que o GlobalExceptionHandler devolve).
    private static final String EX_400 =
        "{\"status\": 400, \"erro\": \"Dados inválidos\", \"campos\": {\"precoAluguel\": \"O preço deve ser maior que zero\"}}";
    private static final String EX_404 = "{\"status\": 404, \"erro\": \"Filme não encontrado\"}";
    private static final String EX_409 = "{\"status\": 409, \"erro\": \"Não é possível excluir um filme que está alugado\"}";
    private static final String EX_500 = "{\"status\": 500, \"erro\": \"Erro interno do servidor\"}";

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
    // Se não existir, lança exceção e o GlobalExceptionHandler responde 404 Not Found.
    @GetMapping("/{id}")
    @Operation(summary = "Busca um filme pelo id")
    @ApiResponse(responseCode = "404", description = "Filme não encontrado",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class),
            examples = @ExampleObject(value = EX_404)))
    public Filme buscar(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Filme não encontrado"));
    }

    // POST /api/filmes -> cadastra um filme e responde 201 Created.
    // @Valid confere as regras da entidade (título obrigatório, preço > 0);
    // se alguma falhar, responde 400 Bad Request sem chegar ao banco.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastra um novo filme")
    @ApiResponse(responseCode = "400", description = "Dados inválidos",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class),
            examples = @ExampleObject(value = EX_400)))
    public Filme criar(@Valid @RequestBody Filme filme) {
        filme.setId(null); // garante que será um INSERT, nunca um UPDATE
        return repository.save(filme); // grava no banco e devolve o filme com o id gerado
    }

    // PUT /api/filmes/{id} -> atualiza TODOS os campos de um filme que já existe.
    // Fluxo: 1) busca o filme (404 se não existir); 2) copia os dados recebidos;
    //        3) salva (o JPA faz UPDATE porque o id já existe) e devolve 200 OK.
    // @Valid reaplica as regras da entidade (título obrigatório, preço > 0) -> 400 se falhar.
    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um filme", description = "Substitui os dados do filme identificado pelo id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Filme atualizado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class),
                examples = @ExampleObject(value = EX_400))),
        @ApiResponse(responseCode = "404", description = "Filme não encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class),
                examples = @ExampleObject(value = EX_404))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class),
                examples = @ExampleObject(value = EX_500)))
    })
    public Filme atualizar(@PathVariable Long id, @Valid @RequestBody Filme dados) {
        Filme filme = repository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Filme não encontrado"));

        // O id vem sempre da URL; o id enviado no corpo (se houver) é ignorado.
        filme.setTitulo(dados.getTitulo());
        filme.setSinopse(dados.getSinopse());
        filme.setGenero(dados.getGenero());
        filme.setAnoLancamento(dados.getAnoLancamento());
        filme.setDuracaoMinutos(dados.getDuracaoMinutos());
        filme.setClassificacao(dados.getClassificacao());
        filme.setImagemUrl(dados.getImagemUrl());
        filme.setPrecoAluguel(dados.getPrecoAluguel());
        // Se "disponivel" não vier no JSON, mantém o valor que já estava no banco.
        if (dados.getDisponivel() != null) {
            filme.setDisponivel(dados.getDisponivel());
        }
        return repository.save(filme);
    }

    // DELETE /api/filmes/{id} -> remove um filme e responde 204 No Content (sem corpo).
    // Regra de negócio: filme alugado no momento (disponivel = false) NÃO pode ser excluído
    // -> responde 409 Conflict. Filme inexistente -> 404.
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Exclui um filme", description = "Não permite excluir filme que está alugado")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Filme excluído"),
        @ApiResponse(responseCode = "404", description = "Filme não encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class),
                examples = @ExampleObject(value = EX_404))),
        @ApiResponse(responseCode = "409", description = "Filme alugado no momento, não pode ser excluído",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class),
                examples = @ExampleObject(value = EX_409))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class),
                examples = @ExampleObject(value = EX_500)))
    })
    public void excluir(@PathVariable Long id) {
        Filme filme = repository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Filme não encontrado"));

        if (Boolean.FALSE.equals(filme.getDisponivel())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Não é possível excluir um filme que está alugado");
        }
        repository.delete(filme);
    }
}
