// Renderiza a tela de músculos: Mostra cabeçalho.
// Se usuário for admin, exibe formulário para adicionar novo músculo.
// Mostra tabela com todos os músculos cadastrados.

function pageMuscles() {
  const isAdmin = S.user?.role === 'ADMIN';
  return `
    <div class="page-header">
      <div class="page-title">MÚS<em>CULOS</em></div>
      <div class="page-sub">Grupos musculares do catálogo</div>
    </div>
    ${isAdmin ? `
    <div class="card mb-12">
      <div class="card-title" style="margin-bottom:14px">Novo Músculo</div>
      <div id="mu-alert" class="alert alert-err hidden"></div>
      <div class="flex gap-12" style="align-items:flex-end">
        <div class="field" style="flex:1"><label>Nome (pt-BR)</label>
          <input id="mu-name" placeholder="ex: Peitoral" /></div>
        <div class="field" style="flex:1"><label>Nome (EN)</label>
          <input id="mu-en" placeholder="ex: Chest" /></div>
        <button class="btn btn-acc btn-sm" id="btn-mu-save">+ Adicionar</button>
      </div>
    </div>` : ''}
    <div class="card">
      <table>
        <thead><tr><th>#</th><th>Nome</th><th>Nome (EN)</th></tr></thead>
        <tbody>
          ${S.muscles.length
            ? S.muscles.map(m=>`
              <tr>
                <td class="mono muted fs-12">${m.id}</td>
                <td class="fw5">${esc(m.name)}</td>
                <td class="muted">${esc(m.nameEn||'—')}</td>
              </tr>`).join('')
            : `<tr><td colspan="3">${empty('🦵','Nenhum músculo cadastrado.')}</td></tr>`
          }
        </tbody>
      </table>
    </div>
  `;
}