// Retorna o HTML da tela principal (dashboard) do sistema.
// Mostra: Saudação personalizada ao usuário, estatísticas globais do sistema, acesso rápido às funções mais usadas

function pageDashboard() {
  const name = (S.user?.name || 'Atleta').split(' ')[0].toUpperCase();
  const { exercises, plans, sessions } = S.stats;
  return `
    <div class="page-header">
      <div class="page-title">OLÁ, <em>${esc(name)}</em></div>
      <div class="page-sub">Bem-vindo de volta — aqui está seu resumo</div>
    </div>
    <div class="stats-grid">
      <div class="stat-card" data-nav="plans">
        <div class="stat-val">${plans}</div>
        <div class="stat-lbl">📋 Planos de Treino</div>
      </div>
      <div class="stat-card" data-nav="sessions">
        <div class="stat-val">${sessions}</div>
        <div class="stat-lbl">🏋️ Sessões Registradas</div>
      </div>
      <div class="stat-card" data-nav="exercises">
        <div class="stat-val">${exercises}</div>
        <div class="stat-lbl">💪 Exercícios</div>
      </div>
      <div class="stat-card" data-nav="records">
        <div class="stat-val">${exercises}</div>
        <div class="stat-lbl">🏆 Personal Records</div>
      </div>
    </div>
    <div class="card">
      <div class="card-head"><div class="card-title">Acesso Rápido</div></div>
      <div style="display:grid;grid-template-columns:1fr 1fr;gap:10px">
        ${[
          ['sessions', '🏋️ Registrar Sessão de Treino'],
          ['plans',    '📋 Criar Novo Plano de Treino'],
          ['records',  '🏆 Consultar Personal Records'],
          ['exercises','💪 Gerenciar Exercícios'],
        ].map(([pg,lbl]) => `
          <button class="btn btn-ghost" data-nav="${pg}"
            style="justify-content:flex-start;padding:13px 16px;font-size:14px">
            ${lbl}
          </button>
        `).join('')}
      </div>
    </div>
  `;
}