// Define o menu lateral e suas seções. Decide qual página mostrar conforme S.page.
// Monta o HTML da tela principal. Registra todos os eventos (cliques, filtros, salvar, etc.) em cada página.
// Controla a navegação "SPA" (Single Page Application) sem recarregar a página.


// Array de objetos, cada um descreve um item do menu lateral (sidebar)
const NAV = [
  { id:'dashboard', label:'Dashboard',       icon:'⬡', section:null },
  { id:'sessions',  label:'Sessões',          icon:'🏋️', section:'Treino' },
  { id:'plans',     label:'Planos',           icon:'📋', section:'Treino' },
  { id:'records',   label:'Personal Records', icon:'🏆', section:'Treino' },
  { id:'exercises', label:'Exercícios',       icon:'💪', section:'Catálogo' },
  { id:'muscles',   label:'Músculos',         icon:'🦵', section:'Catálogo' },
  { id:'equipment', label:'Equipamentos',     icon:'🔩', section:'Catálogo' },
  { id:'users',     label:'Usuários',         icon:'👤', section:'Admin' },
];

//Constroi o HTML do menu lateral, incluindo seções, itens e informações do usuário.
function renderSidebar() {
  const sections  = [...new Set(NAV.map(n => n.section))];
  const items = sections.map(sec => `
    ${sec ? `<div class="nav-section">${sec}</div>` : ''}
    ${NAV.filter(n => n.section === sec).map(n => `
      <div class="nav-item ${S.page === n.id ? 'active' : ''}" data-nav="${n.id}">
        <span class="nav-icon">${n.icon}</span>${n.label}
      </div>
    `).join('')}
  `).join('');

  const initial   = (S.user?.name || 'U')[0].toUpperCase();
  const firstName = (S.user?.name || S.user?.email || '').split(' ')[0];

  return `
    <div class="sidebar-logo">
      ACT<span>IVE</span>
      <small>Gestão de Treinos</small>
    </div>
    <nav class="nav">${items}</nav>
    <div class="sidebar-footer">
      <div class="user-chip">
        <div class="user-avatar">${initial}</div>
        <div>
          <div class="user-name">${esc(firstName)}</div>
          <div class="user-role">${S.user?.role || 'USER'}</div>
        </div>
      </div>
      <button class="btn btn-ghost btn-sm" style="width:100%" id="btn-logout">Sair</button>
    </div>
  `;
}
//Decidi qual função de página chamar para renderizar o conteúdo principal, baseado em S.page.
function renderPage() {
  switch (S.page) {
    case 'dashboard': return pageDashboard();
    case 'exercises': return pageExercises();
    case 'muscles':   return pageMuscles();
    case 'equipment': return pageEquipment();
    case 'plans':     return pagePlans();
    case 'sessions':  return pageSessions();
    case 'records':   return pageRecords();
    case 'users':     return pageUsers();
    default:          return pageDashboard();
  }
}

// Ponto de entrada chamado por setState()
function render() {
  const app = document.getElementById('app');
  if (!S.token) {
    app.innerHTML = S.page === 'register' ? pageRegister() : pageLogin();
    bindLogin();
    return;
  }
  app.innerHTML = `
    <div class="sidebar">${renderSidebar()}</div>
    <main class="main" id="main-content">${renderPage()}</main>
  `;
  bindPage();
}

// Registra os eventos conforme da página atual ─
function bindPage() {
  document.querySelectorAll('[data-nav]').forEach(el =>
    el.addEventListener('click', () => { S.page = el.dataset.nav; render(); })
  );
  document.getElementById('btn-logout')?.addEventListener('click', logout);

  if (S.page === 'exercises') {
    loadExercises();
    document.getElementById('flt-cat')?.addEventListener('change',
      () => loadExercises(currentExFilters()));
    document.getElementById('flt-muscle')?.addEventListener('change',
      () => loadExercises(currentExFilters()));
    document.getElementById('flt-eq')?.addEventListener('change',
      () => loadExercises(currentExFilters()));
    document.getElementById('btn-ex-clear')?.addEventListener('click', () => {
      document.getElementById('flt-cat').value    = '';
      document.getElementById('flt-muscle').value = '';
      document.getElementById('flt-eq').value     = '';
      loadExercises();
    });
    document.getElementById('btn-ex-new')?.addEventListener('click', () => openExerciseForm());
  }

  if (S.page === 'muscles') {
    document.getElementById('btn-mu-save')?.addEventListener('click', async () => {
      const btn = document.getElementById('btn-mu-save');
      btn.disabled = true; btn.innerHTML = spinner();
      clearAlert('mu-alert');
      try {
        await api.createMuscle({
          name:   document.getElementById('mu-name').value.trim(),
          nameEn: document.getElementById('mu-en').value.trim(),
        });
        const res = await api.listMuscles({ size:200 });
        S.muscles = res?.content ?? res ?? [];
        toast('Músculo adicionado.', 'ok', 3000);
        render();
      } catch(e) {
        alert$('mu-alert', saveErrorMsg(e, 'músculo'));
        btn.disabled = false; btn.textContent = '+ Adicionar';
      }
    });
  }

  if (S.page === 'equipment') {
    document.getElementById('btn-eq-save')?.addEventListener('click', async () => {
      const btn = document.getElementById('btn-eq-save');
      btn.disabled = true; btn.innerHTML = spinner();
      clearAlert('eq-alert');
      try {
        await api.createEquipment({ name: document.getElementById('eq-name').value.trim() });
        const res = await api.listEquipments({ size:200 });
        S.equipments = res?.content ?? res ?? [];
        toast('Equipamento adicionado.', 'ok', 3000);
        render();
      } catch(e) {
        alert$('eq-alert', saveErrorMsg(e, 'equipamento'));
        btn.disabled = false; btn.textContent = '+ Adicionar';
      }
    });
  }

  if (S.page === 'plans') {
    loadPlans();
    document.getElementById('flt-plan-name')?.addEventListener('input',
      () => loadPlans(currentPlanFilters()));
    document.getElementById('flt-plan-goal')?.addEventListener('change',
      () => loadPlans(currentPlanFilters()));
    document.getElementById('btn-plan-clear')?.addEventListener('click', () => {
      document.getElementById('flt-plan-name').value = '';
      document.getElementById('flt-plan-goal').value = '';
      loadPlans();
    });
    document.getElementById('btn-plan-new')?.addEventListener('click', openPlanWizard);
  }

  if (S.page === 'sessions') {
    loadSessions();
    const rebind = () => loadSessions({
      fromDate:   document.getElementById('flt-ss-from')?.value  || undefined,
      toDate:     document.getElementById('flt-ss-to')?.value    || undefined,
      planName:   document.getElementById('flt-ss-plan')?.value  || undefined,
      exerciseId: document.getElementById('flt-ss-ex')?.value    || undefined,
    });
    document.getElementById('flt-ss-from')?.addEventListener('change', rebind);
    document.getElementById('flt-ss-to')?.addEventListener('change', rebind);
    document.getElementById('flt-ss-plan')?.addEventListener('input', rebind);
    document.getElementById('flt-ss-ex')?.addEventListener('change', rebind);
    document.getElementById('btn-ss-clear')?.addEventListener('click', () => {
      ['flt-ss-from','flt-ss-to','flt-ss-plan','flt-ss-ex'].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = '';
      });
      loadSessions();
    });
    document.getElementById('btn-ss-new')?.addEventListener('click', openSessionForm);
  }

  if (S.page === 'records') {
    document.getElementById('pr-select')?.addEventListener('change', e => {
      if (e.target.value) loadPR(e.target.value);
      else document.getElementById('pr-result').innerHTML =
        empty('🏆','Selecione um exercício para ver o PR.');
    });
  }

  if (S.page === 'users') {
    loadUsers();
    document.getElementById('flt-usr-name')?.addEventListener('input',
      () => loadUsers(currentUserFilters()));
    document.getElementById('flt-usr-email')?.addEventListener('input',
      () => loadUsers(currentUserFilters()));
    document.getElementById('btn-usr-clear')?.addEventListener('click', () => {
      document.getElementById('flt-usr-name').value  = '';
      document.getElementById('flt-usr-email').value = '';
      loadUsers();
    });
  }
}