package com.rentafilm.backend.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

// Modelo usado SÓ para documentar (Swagger/OpenAPI) o corpo das respostas de erro.
// O GlobalExceptionHandler devolve JSON exatamente nesse formato.
@Schema(description = "Corpo padrão das respostas de erro da API")
public record ErroResposta(

    @Schema(description = "Código HTTP do erro", example = "404")
    int status,

    @Schema(description = "Mensagem explicando o erro", example = "Filme não encontrado")
    String erro,

    @Schema(description = "Somente no erro 400 de validação: nome do campo -> mensagem")
    Map<String, String> campos
) {}
