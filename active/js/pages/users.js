// Renderiza e gerencia a tela de gestão de usuários da plataforma:
// Permite filtrar usuários por nome e email
// Exibe tabela com informações principais dos usuários
// Admin pode remover usuários por botão

function pageUsers() {
  return `
    <div class="page-header">
      <div class="page-title">USUÁ<em>RIOS</em></div>
      <div class="page-sub">Gestão de usuários cadastrados na plataforma</div>
    </div>
    <div class="filter-bar">
      <div class="field" style="flex:1"><label>Nome</label>
        <input id="flt-usr-name" placeholder="Buscar por nome..." /></div>
      <div class="field" style="flex:1"><label>Email</label>
        <input id="flt-usr-email" placeholder="Buscar por email..." /></div>
      <button class="btn btn-ghost btn-sm" id="btn-usr-clear">Limpar</button>
    </div>
    <div class="card" id="usr-table-wrap">
      <div class="loading-box">${spinner()}</div>
    </div>
  `;
}

function currentUserFilters() {
  return {
    name:  document.getElementById('flt-usr-name')?.value  || undefined,
    email: document.getElementById('flt-usr-email')?.value || undefined,
  };
}

async function loadUsers(params = {}) {
  const wrap = document.getElementById('usr-table-wrap');
  if (!wrap) return;
  wrap.innerHTML = `<div class="loading-box">${spinner()}</div>`;
  try {
    const res     = await api.listUsers(params);
    const data    = res?.content ?? res ?? [];
    const isAdmin = S.user?.role === 'ADMIN';
    if (!data.length) { wrap.innerHTML = empty('👤','Nenhum usuário encontrado.'); return; }
    const LVL = {BEGINNER:'bg', INTERMEDIATE:'bb', ADVANCED:'br'};
    wrap.innerHTML = `
      <table>
        <thead>
          <tr>
            <th>Nome</th><th>Email</th><th>Nível</th>
            <th>Peso</th><th>Altura</th><th>Idade</th>
            ${isAdmin ? '<th>Ações</th>' : ''}
          </tr>
        </thead>
        <tbody>
          ${data.map(u=>`
            <tr>
              <td class="fw5">${esc(u.name)}</td>
              <td class="muted fs-12">${esc(u.email)}</td>
              <td><span class="badge ${LVL[u.trainingLevel]||'bk'}">${humanize(u.trainingLevel)}</span></td>
              <td class="mono">${u.weight ? u.weight+'kg' : '—'}</td>
              <td class="mono">${u.height ? u.height+'m'  : '—'}</td>
              <td class="mono">${u.age||'—'}</td>
              ${isAdmin ? `<td>
                <button class="btn btn-danger btn-xs" data-del-user="${u.id}">Excluir</button>
              </td>` : ''}
            </tr>
          `).join('')}
        </tbody>
      </table>
    `;
    if (isAdmin) {
      wrap.querySelectorAll('[data-del-user]').forEach(btn =>
        btn.addEventListener('click', async () => {
          if (!confirm('Excluir usuário? Esta ação não pode ser desfeita.')) return;
          try {
            await api.deleteUser(+btn.dataset.delUser);
            toast('Usuário excluído.', 'ok', 3000);
            loadUsers(currentUserFilters());
          } catch(err) {
            toast(deleteErrorMsg(err, 'usuário'));
          }
        })
      );
    }
  } catch(e) {
    wrap.innerHTML = `<div class="alert alert-err">${esc(loadErrorMsg(e, 'usuários'))}</div>`;
  }
}