// =====================================================================
// admin.js - Página de gerenciamento de filmes (admin.html).
// Usa as rotas da Integrante 4: PUT /api/filmes/{id} (editar) e
// DELETE /api/filmes/{id} (excluir), e a rota GET para listar.
// Precisa do api.js carregado antes.
// =====================================================================

let filmes = [];          // cópia local da lista, usada para preencher o formulário de edição
let modalEditar = null;   // instância do modal do Bootstrap

// Mostra uma mensagem no topo da página (tipo: success, danger, warning...).
function mostrarMensagem(texto, tipo) {
  document.getElementById("mensagem").innerHTML =
    `<div class="alert alert-${tipo} alert-dismissible fade show" role="alert">
       ${esc(texto)}
       <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
     </div>`;
}

// Busca os filmes na API e desenha a tabela.
async function carregarTabela() {
  const corpo = document.getElementById("tabela-filmes");
  try {
    filmes = await listarFilmes();
  } catch (e) {
    corpo.innerHTML = "";
    mostrarMensagem(e.message, "danger");
    return;
  }

  if (filmes.length === 0) {
    corpo.innerHTML = '<tr><td colspan="6" class="text-center text-muted">Nenhum filme cadastrado.</td></tr>';
    return;
  }

  corpo.innerHTML = filmes.map((f) => `
    <tr>
      <td>${f.id}</td>
      <td>${esc(f.titulo)}</td>
      <td>${esc(f.genero)}</td>
      <td>R$ ${Number(f.precoAluguel).toFixed(2).replace(".", ",")}</td>
      <td>${f.disponivel ? '<span class="badge bg-success">Disponível</span>' : '<span class="badge bg-secondary">Alugado</span>'}</td>
      <td class="text-end text-nowrap">
        <button class="btn btn-sm btn-outline-light" data-acao="editar" data-id="${f.id}">Editar</button>
        <button class="btn btn-sm btn-outline-danger" data-acao="excluir" data-id="${f.id}">Excluir</button>
      </td>
    </tr>`).join("");
}

// Clique em "Editar": preenche o formulário do modal com os dados do filme e abre o modal.
function abrirEdicao(id) {
  const f = filmes.find((x) => x.id === id);
  if (!f) return;

  document.getElementById("f-id").value = f.id;
  document.getElementById("f-titulo").value = f.titulo ?? "";
  document.getElementById("f-sinopse").value = f.sinopse ?? "";
  document.getElementById("f-genero").value = f.genero ?? "";
  document.getElementById("f-ano").value = f.anoLancamento ?? "";
  document.getElementById("f-duracao").value = f.duracaoMinutos ?? "";
  document.getElementById("f-classificacao").value = f.classificacao ?? "";
  document.getElementById("f-imagem").value = f.imagemUrl ?? "";
  document.getElementById("f-preco").value = f.precoAluguel ?? "";
  document.getElementById("f-disponivel").checked = !!f.disponivel;
  document.getElementById("erro-modal").classList.add("d-none");

  modalEditar.show();
}

// Envio do formulário: chama PUT /api/filmes/{id} com todos os campos.
async function salvarEdicao(evento) {
  evento.preventDefault();
  const id = document.getElementById("f-id").value;
  const valorNumero = (campo) => {
    const v = document.getElementById(campo).value;
    return v === "" ? null : Number(v);
  };

  const filme = {
    titulo: document.getElementById("f-titulo").value,
    sinopse: document.getElementById("f-sinopse").value,
    genero: document.getElementById("f-genero").value,
    anoLancamento: valorNumero("f-ano"),
    duracaoMinutos: valorNumero("f-duracao"),
    classificacao: document.getElementById("f-classificacao").value,
    imagemUrl: document.getElementById("f-imagem").value,
    precoAluguel: valorNumero("f-preco"),
    disponivel: document.getElementById("f-disponivel").checked,
  };

  try {
    await atualizarFilme(id, filme);
    modalEditar.hide();
    mostrarMensagem("Filme atualizado com sucesso.", "success");
    await carregarTabela();
  } catch (e) {
    // Ex.: 400 "Dados inválidos: O preço deve ser maior que zero" aparece dentro do modal.
    const erro = document.getElementById("erro-modal");
    erro.textContent = e.message;
    erro.classList.remove("d-none");
  }
}

// Clique em "Excluir": pede confirmação e chama DELETE /api/filmes/{id}.
async function excluir(id) {
  const f = filmes.find((x) => x.id === id);
  if (!confirm(`Deseja realmente excluir "${f ? f.titulo : id}"?`)) return;

  try {
    await excluirFilme(id);
    mostrarMensagem("Filme excluído com sucesso.", "success");
    await carregarTabela();
  } catch (e) {
    // Ex.: 409 "Não é possível excluir um filme que está alugado".
    mostrarMensagem(e.message, "warning");
  }
}

document.addEventListener("DOMContentLoaded", () => {
  modalEditar = new bootstrap.Modal(document.getElementById("modal-editar"));
  document.getElementById("form-editar").addEventListener("submit", salvarEdicao);

  // Um único ouvinte de clique na tabela trata os botões Editar e Excluir.
  document.getElementById("tabela-filmes").addEventListener("click", (ev) => {
    const botao = ev.target.closest("button[data-acao]");
    if (!botao) return;
    const id = Number(botao.dataset.id);
    if (botao.dataset.acao === "editar") abrirEdicao(id);
    if (botao.dataset.acao === "excluir") excluir(id);
  });

  carregarTabela();
});
