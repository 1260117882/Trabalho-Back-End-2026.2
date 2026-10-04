// =====================================================================
// catalogo.js - Catálogo da página inicial (index.html).
// Substitui os dados fixos por filmes reais vindos da API (GET /api/filmes).
// Precisa do api.js carregado antes e de um <div id="lista-filmes"> na página.
// =====================================================================

// Monta o HTML de um card de filme (Bootstrap).
function cardFilme(f) {
  const status = f.disponivel
    ? '<span class="badge bg-success">Disponível</span>'
    : '<span class="badge bg-secondary">Alugado</span>';

  return `
    <div class="col-sm-6 col-lg-3">
      <div class="card h-100" style="background: var(--surface, #171a21)">
        <img src="${urlImagem(f.imagemUrl)}" class="card-img-top" alt="${esc(f.titulo)}"
             style="height: 340px; object-fit: cover"
             onerror="this.onerror=null; this.src='imagens/filme1.jpg'">
        <div class="card-body">
          <h3 class="h5 card-title">${esc(f.titulo)}</h3>
          <p class="card-text small text-muted mb-2">
            ${esc(f.genero)} • ${esc(f.anoLancamento)} • ${esc(f.duracaoMinutos)} min
            <span class="badge border ms-1">${esc(f.classificacao)}</span>
          </p>
          <p class="card-text small">${esc(f.sinopse)}</p>
        </div>
        <div class="card-footer d-flex justify-content-between align-items-center">
          <strong>R$ ${Number(f.precoAluguel).toFixed(2).replace(".", ",")}</strong>
          ${status}
        </div>
      </div>
    </div>`;
}

// Busca os filmes na API e desenha os cards na página.
async function carregarCatalogo() {
  const lista = document.getElementById("lista-filmes");
  if (!lista) return;

  lista.innerHTML = '<p class="text-muted">Carregando filmes...</p>';
  try {
    const filmes = await listarFilmes();
    lista.innerHTML = filmes.length
      ? filmes.map(cardFilme).join("")
      : '<p class="text-muted">Nenhum filme cadastrado ainda.</p>';
  } catch (e) {
    lista.innerHTML = `<p class="text-danger">${esc(e.message)}</p>`;
  }
}

document.addEventListener("DOMContentLoaded", carregarCatalogo);
