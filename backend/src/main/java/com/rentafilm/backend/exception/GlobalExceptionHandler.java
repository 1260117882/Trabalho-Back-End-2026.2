package com.rentafilm.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

// Centraliza o tratamento de erros: toda falha vira um JSON padronizado
// {"status": 400, "erro": "mensagem", "campos": {...}} com o código HTTP adequado.
// basePackages limita o handler aos controllers, para não interferir no Swagger (/v3/api-docs).
@RestControllerAdvice(basePackages = "com.rentafilm.backend.controller")
public class GlobalExceptionHandler {

    // Monta o corpo padrão da resposta de erro.
    private Map<String, Object> corpo(HttpStatus status, String mensagem) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("status", status.value());
        corpo.put("erro", mensagem);
        return corpo;
    }

    // 400 - falha no @Valid (ex.: título em branco, preço <= 0). Lista cada campo com problema.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
            .forEach(e -> campos.put(e.getField(), e.getDefaultMessage()));
        Map<String, Object> corpo = corpo(HttpStatus.BAD_REQUEST, "Dados inválidos");
        corpo.put("campos", campos);
        return ResponseEntity.badRequest().body(corpo);
    }

    // 400 - JSON malformado ou com tipo errado (ex.: "precoAluguel": "abc").
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonInvalido(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(corpo(HttpStatus.BAD_REQUEST, "JSON inválido ou malformado"));
    }

    // 400 - id da URL que não é número (ex.: /api/filmes/abc).
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTipoInvalido(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest().body(corpo(HttpStatus.BAD_REQUEST, "Parâmetro inválido: " + ex.getName()));
    }

    // 404 / 409 etc. - erros lançados de propósito no controller com ResponseStatusException.
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleStatus(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String mensagem = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
        return ResponseEntity.status(status).body(corpo(status, mensagem));
    }

    // 500 - qualquer erro inesperado. Não expõe detalhes internos ao cliente.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(corpo(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor"));
    }
}
