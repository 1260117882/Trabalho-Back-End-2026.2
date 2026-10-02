package com.rentafilm.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

// Entidade: representa a tabela "filmes" do banco de dados
@Entity
@Table(name = "filmes")
@Getter @Setter
public class Filme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // id gerado pelo banco (BIGSERIAL)
    private Long id;

    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    private String sinopse;
    private String genero;

    @Column(name = "ano_lancamento")
    private Integer anoLancamento;

    @Column(name = "duracao_minutos")
    private Integer duracaoMinutos;

    private String classificacao;

    @Column(name = "imagem_url")
    private String imagemUrl;

    @NotNull(message = "O preço do aluguel é obrigatório")
    @Positive(message = "O preço deve ser maior que zero")
    @Column(name = "preco_aluguel")
    private BigDecimal precoAluguel;

    private Boolean disponivel = true;
}