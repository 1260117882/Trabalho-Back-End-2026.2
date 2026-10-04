// =====================================================================
// api.js - Funções que conversam com a API de filmes (Spring Boot).
// Usado pelo catálogo (index.html) e pela página de gerenciamento (admin.html).
// Este código roda no NAVEGADOR, por isso o endereço é localhost:8080
// (e não "backend:8080", que só existe dentro da rede do Docker).
// =====================================================================

const API_URL = "http://localhost:8080/api/filmes";

// O backend devolve só o nome do arquivo (ex.: "filme1.jpg"); aqui montamos o caminho.
// As imagens ficam em public/imagens/.
const urlImagem = (nome) => `imagens/${encodeURIComponent(nome || "filme1.jpg")}`;

// Escapa caracteres especiais de HTML, para um título com "<" ou "&" não quebrar a página.
function esc(texto) {
  return String(texto ?? "").replace(/[&<>"']/g, (c) => (
    { "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]
  ));
}

// Faz a chamada HTTP e trata os erros de forma única para todas as rotas.
// - Resposta 2xx: devolve o JSON (ou null no 204 No Content, que não tem corpo).
// - Resposta de erro (400, 404, 409, 500): lê o JSON {status, erro, campos} da API
//   e lança uma Error com a mensagem pronta para mostrar ao usuário.
// - API fora do ar: lança uma Error explicando que não conseguiu conectar.
async function chamarApi(url, opcoes) {
  let resposta;
  try {
    resposta = await fetch(url, opcoes);
  } catch (e) {
    throw new Error("Não foi possível conectar à API. O backend está rodando?");
  }

  if (resposta.ok) {
    return resposta.status === 204 ? null : resposta.json();
  }

  let mensagem = `Erro ${resposta.status}`;
  try {
    const erro = await resposta.json();
    mensagem = erro.erro || mensagem;
    // No 400 de validação, a API lista o problema de cada campo.
    if (erro.campos) mensagem += ": " + Object.values(erro.campos).join(", ");
  } catch (e) { /* resposta sem JSON: mantém a mensagem padrão */ }
  throw new Error(mensagem);
}

// GET /api/filmes -> lista todos os filmes (rota da Integrante 3)
function listarFilmes() {
  return chamarApi(API_URL);
}

// PUT /api/filmes/{id} -> atualiza um filme (envia o objeto inteiro em JSON)
function atualizarFilme(id, filme) {
  return chamarApi(`${API_URL}/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(filme),
  });
}

// DELETE /api/filmes/{id} -> exclui um filme (resposta 204, sem corpo)
function excluirFilme(id) {
  return chamarApi(`${API_URL}/${id}`, { method: "DELETE" });
}

// Permite testar este arquivo no Node (não faz nada no navegador).
if (typeof module !== "undefined") {
  module.exports = { API_URL, urlImagem, esc, chamarApi, listarFilmes, atualizarFilme, excluirFilme };
}
