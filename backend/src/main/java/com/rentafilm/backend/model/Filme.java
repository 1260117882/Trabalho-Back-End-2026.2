package com.rentafilm.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

// Entidade JPA: cada objeto Filme representa uma linha da tabela "filmes" no PostgreSQL.
@Entity
@Table(name = "filmes")
// Lombok: gera automaticamente os getters e setters de todos os campos.
@Getter @Setter
public class Filme {

    // Chave primária da tabela. O banco gera o valor sozinho (coluna BIGSERIAL).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Título do filme. Obrigatório: se vier vazio ou ausente, a API responde 400.
    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    // Resumo da história exibido na página do filme.
    private String sinopse;

    // Gênero principal (ex.: "Ficção científica"), usado nos filtros do catálogo.
    private String genero;

    // @Column liga o campo Java (anoLancamento) à coluna do banco (ano_lancamento).
    @Column(name = "ano_lancamento")
    private Integer anoLancamento;

    // Duração total do filme, em minutos.
    @Column(name = "duracao_minutos")
    private Integer duracaoMinutos;

    // Classificação indicativa (ex.: "10", "14", "L"). É texto para aceitar "L".
    private String classificacao;

    // Nome do arquivo ou URL da capa do filme (ex.: "filme1.jpg").
    @Column(name = "imagem_url")
    private String imagemUrl;

    // Preço do aluguel temporário. Obrigatório e maior que zero.
    // BigDecimal evita erros de arredondamento em valores de dinheiro.
    @NotNull(message = "O preço do aluguel é obrigatório")
    @Positive(message = "O preço deve ser maior que zero")
    @Column(name = "preco_aluguel")
    private BigDecimal precoAluguel;

    // Indica se o filme está disponível para aluguel. Começa como true.
    private Boolean disponivel = true;
}